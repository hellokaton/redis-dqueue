package io.github.biezhi.redisdqueue.configuration;

import io.github.biezhi.redisdqueue.core.Callback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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

	private int retryInterval = 10;

	private int taskTtl = 24 * 3600;

	private int callbackTtl = 3;

	private int maxJobCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	private int maxCallbackCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	/**
	 * Delay of message consumer callback processing mapping.
	 * <p>
	 * ::key   -> TOPIC
	 * ::VALUE -> CALLBACK INSTANCE
	 */
	private Map<String, Callback> callbacks = new ConcurrentHashMap<>();

	/**
	 * The key in the processing in a single JVM is managed in the collection,
	 * to ensure that only a task in the implementation
	 */
	private Set<String> processedKeys = new CopyOnWriteArraySet<>();

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

	public boolean isProcessing(String key) {
		return processedKeys.contains(key);
	}

	public boolean waitProcessing(String key) {
		return !isProcessing(key);
	}

	public void addProcessed(String key) {
		processedKeys.add(key);
	}

	public void processed(String key) {
		processedKeys.remove(key);
	}

}
