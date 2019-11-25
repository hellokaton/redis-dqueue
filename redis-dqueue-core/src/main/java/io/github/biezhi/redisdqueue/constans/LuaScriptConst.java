package io.github.biezhi.redisdqueue.constans;

/**
 * LuaScriptConst
 *
 * @author biezhi
 * @date 2019/11/25
 */
public interface LuaScriptConst {

	String PUSH_MESSAGE = "" +
			"local hash_key = KEYS[1]\n" +
			"local zset_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local hash_value = ARGV[1]\n" +
			"local zset_score = ARGV[2]\n" +
			"local result = redis.call('ZADD', zset_key, zset_score, id_)\n" +
			"redis.call('HSET', hash_key, id_, hash_value)\n" +
			"return result;";

	String TRANSFER_MESSAGE = "" +
			"local from_key = KEYS[1]\n" +
			"local to_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local zset_score = ARGV[1]\n" +
			"redis.call('ZADD', to_key, zset_score, id_)\n" +
			"local result = redis.call('ZREM', from_key, id_)\n" +
			"return result;";

}