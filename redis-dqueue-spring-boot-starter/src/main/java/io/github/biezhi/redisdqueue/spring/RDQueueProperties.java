package io.github.biezhi.redisdqueue.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * RDQueueProperties
 *
 * @author biezhi
 * @date 2019/11/22
 */
@Data
@ConfigurationProperties(prefix = "rdqueue")
public class RDQueueProperties {

	private static final String DEFAULT_DQUEUE_NAME = "delay-queue-";

	private String dqueuePrefix = DEFAULT_DQUEUE_NAME;

	private String redisURI = "redis://localhost/";

	private boolean hasPassword = false;

	private String password = "";

	private String host = "localhost";

	private int port = 6379;

	private boolean overrideUpdate = false;

	@Deprecated
	private List<String> cluster;

	private int retryInterval = 10;

	private int callbackTtl = 3;

	private int taskTtl = 24 * 3600;

	private int maxJobCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	private int maxCallbackCoreSize = Runtime.getRuntime().availableProcessors() * 2;

}
