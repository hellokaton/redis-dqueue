package io.github.biezhi.redisdqueue.core;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DQRedis
 *
 * @author biezhi
 * @date 2019/11/25
 */
public class DQRedis {

	private RedisClient redisClient;

	private RedisClusterClient clusterClient;

	private StatefulRedisConnection<String, String> connection;

	private StatefulRedisClusterConnection<String, String> clusterConnection;

	private boolean isCluster;

	public DQRedis(String redisURI, List<String> cluster) {
		if (null != cluster) {
			List<RedisURI> nodes = cluster.stream()
					.map(RedisURI::create)
					.collect(Collectors.toList());
			this.clusterClient = RedisClusterClient.create(nodes);
			this.isCluster = true;
			this.clusterConnection = clusterClient.connect();
		} else {
			this.redisClient = RedisClient.create(redisURI);
			this.connection = redisClient.connect();
		}
	}

	public DQRedis(RedisURI redisURI, List<String> cluster) {
		if (null != cluster) {
			List<RedisURI> nodes = cluster.stream()
					.map(RedisURI::create)
					.collect(Collectors.toList());
			this.clusterClient = RedisClusterClient.create(nodes);
			this.isCluster = true;
			this.clusterConnection = clusterClient.connect();
		} else {
			this.redisClient = RedisClient.create(redisURI);
			this.connection = redisClient.connect();
		}
	}

	public Long syncEval(String script, ScriptOutputType type, String[] keys, String... values) {
		if (isCluster) {
			return clusterConnection.sync().eval(script, type, keys, values);
		} else {
			return connection.sync().eval(script, type, keys, values);
		}
	}

	public List<String> zrangebyscore(String key, Range<? extends Number> range, Limit limit) {
		if (isCluster) {
			return clusterConnection.sync().zrangebyscore(key, range, limit);
		} else {
			return connection.sync().zrangebyscore(key, range, limit);
		}
	}

	public String hget(String key, String feild) {
		if (isCluster) {
			return clusterConnection.sync().hget(key, feild);
		} else {
			return connection.sync().hget(key, feild);
		}
	}

	public boolean hset(String key, String feild, String value) {
		if (isCluster) {
			return clusterConnection.sync().hset(key, feild, value);
		} else {
			return connection.sync().hset(key, feild, value);
		}
	}

	public Long zrem(String key, String... members) {
		if (isCluster) {
			return clusterConnection.sync().zrem(key, members);
		} else {
			return connection.sync().zrem(key, members);
		}
	}

	public Long hdel(String key, String feild) {
		if (isCluster) {
			return clusterConnection.sync().hdel(key, feild);
		} else {
			return connection.sync().hdel(key, feild);
		}
	}

	public <T> RedisFuture<T> asyncEval(String script, ScriptOutputType type, String[] keys, String... values) {
		if (isCluster) {
			return clusterConnection.async().eval(script, type, keys, values);
		} else {
			return connection.async().eval(script, type, keys, values);
		}
	}

	void shutdown() {
		if (isCluster) {
			clusterConnection.close();
			clusterClient.shutdown();
		} else {
			connection.close();
			redisClient.shutdown();
		}
	}

}
