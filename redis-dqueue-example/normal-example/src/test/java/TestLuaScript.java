import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author biezhi
 * @date 2019/11/25
 */
@Slf4j
public class TestLuaScript {

	private static String RAW_LUA = "local key = KEYS[1]\n" +
			"local value = ARGV[1]\n" +
			"local timeout = ARGV[2]\n" +
			"redis.call('SETEX', key, 10, value)\n" +
			"local result = redis.call('GET', key)\n" +
			"return result;";

	private static String LUA_S = "" +
			"local key1 = KYES[1]\n" +
			"local key2 = KYES[2]\n" +
			"local key3 = KYES[3]\n" +
			"local hash_key = KYES[4]\n" +
			"local message = ARGV[1]\n" +
			"local retries = ARGV[2]\n" +
			"local timestamp = ARGV[3]\n" +
			"redis.call('HSET', key1, hash_key, message)\n" +
			"redis.call('HSET', key2, hash_key, retries)\n" +
			"redis.call('ZADD', key3, timestamp, hash_key)";

	private static AtomicReference<String> LUA_SHA = new AtomicReference<>();

	public static void main(String[] args22) {
		RedisClient client = RedisClient.create("redis://localhost/");

		StatefulRedisConnection<String, String> connect  = client.connect();
		RedisCommands<String, String>           commands = connect.sync();

		LUA_SHA.compareAndSet(null, commands.scriptLoad(RAW_LUA));

		String[] keys = new String[]{"name"};
		String[] args = new String[]{"throwable", "5000"};

		String result = commands.eval(LUA_S, ScriptOutputType.VALUE, keys, args);

		log.info("Get value:{}", result);

		connect.close();
		client.shutdown();
	}
}
