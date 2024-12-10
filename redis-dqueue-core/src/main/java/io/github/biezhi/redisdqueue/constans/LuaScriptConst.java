package io.github.biezhi.redisdqueue.constans;

/**
 * LuaScriptConst
 *
 * @author biezhi
 * @date 2019/11/25
 */
public interface LuaScriptConst {

	String PUSH_MESSAGE =
			"local hash_key = KEYS[1]\n" +
			"local zset_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local hash_value = ARGV[1]\n" +
			"local zset_score = ARGV[2]\n" +
			"if redis.call('ZADD', zset_key, zset_score, id_) == 1 then\n" +
			"   redis.call('HSET', hash_key, id_, hash_value)\n" +
			"   return 1;\n" +
			"else\n" +
			"   return 0;\n" +
			"end";

	String ALL_UPDATE_PUSH_MESSAGE =
			"local hash_key = KEYS[1]\n" +
			"local zset_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local hash_value = ARGV[1]\n" +
			"local zset_score = ARGV[2]\n" +
			"redis.call('ZADD', zset_key, zset_score, id_)\n" +
			"redis.call('HSET', hash_key, id_, hash_value)\n" +
			"return 1;\n";

	String  TRANSFER_MESSAGE =
			"local from_key = KEYS[1]\n" +
			"local to_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local zset_score = ARGV[1]\n" +
			"if redis.call('ZREM', from_key, id_) == 1 then\n" +
			"   redis.call('ZADD', to_key, zset_score, id_)\n" +
			"	return 1;\n" +
			"else\n" +
			"	return 0;\n" +
			"end";

	String  ALL_UPDATE_TRANSFER_MESSAGE =
			"local from_key = KEYS[1]\n" +
			"local to_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local zset_score = ARGV[1]\n" +
			"redis.call('ZREM', from_key, id_) \n" +
			"redis.call('ZADD', to_key, zset_score, id_)\n" +
			"return 1;\n";

}