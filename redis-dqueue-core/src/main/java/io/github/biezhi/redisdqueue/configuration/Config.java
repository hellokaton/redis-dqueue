package io.github.biezhi.redisdqueue.configuration;

import io.github.biezhi.redisdqueue.core.Callback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Config
 *
 * @author biezhi
 * @date 2019/11/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {

	private static final String DEFAULT_DQUEUE_PREFIX_NAME = "delay-queue-";

	private String keyPrefix = DEFAULT_DQUEUE_PREFIX_NAME;

	private String redisURI = "redis://localhost/";

	private List<String> cluster;

	private int fetchBeforeSeconds = 30 * 60;

	private int retryInterval = 10;

	private int taskTtl = 24 * 3600;

	private int maxJobCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	private int maxCallbackCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	/**
	 * Delay of message consumer callback processing mapping.
	 * <p>
	 * ::key   -> TOPIC
	 * ::VALUE -> CALLBACK INSTANCE
	 */
	private Map<String, Callback> callbacks = new ConcurrentHashMap<>();

	public String getDelayKey() {
		return this.keyPrefix + "keys";
	}

	public String getAckKey() {
		return this.keyPrefix + "acks";
	}

	public String getErrorKey() {
		return this.keyPrefix + "errors";
	}

	public String getHashKey() {
		return this.keyPrefix + "hash";
	}

}
