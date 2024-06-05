package io.github.biezhi.redisdqueue.spring;

import io.github.biezhi.redisdqueue.configuration.Config;
import io.github.biezhi.redisdqueue.core.RDQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RDQueueAutoConfiguration
 *
 * @author biezhi
 * @date 2019/11/21
 */
@Configuration
@EnableConfigurationProperties(RDQueueProperties.class)
public class RDQueueAutoConfiguration {

	@Autowired
	private RDQueueProperties properties;

	@Bean
	@ConditionalOnMissingBean
	public Config dqConfig() {
		Config config = new Config();
		config.setRedisURI(properties.getRedisURI());
		config.setHasPassword(properties.isHasPassword());
		config.setHost(properties.getHost());
		config.setPort(properties.getPort());
		config.setPassword(properties.getPassword());
		config.setKeyPrefix(properties.getDqueuePrefix());
		config.setCallbackTtl(properties.getCallbackTtl());
		config.setTaskTtl(properties.getTaskTtl());
		config.setMaxJobCoreSize(properties.getMaxJobCoreSize());
		config.setMaxCallbackCoreSize(properties.getMaxCallbackCoreSize());
		return config;
	}

	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean
	public RDQueue rdQueue(@Autowired Config config) {
		return new RDQueue(config);
	}

	@Bean
	@ConditionalOnMissingBean
	public RDQueueTemplate rdQueueTemplate(@Autowired RDQueue rdQueue) {
		return new RDQueueTemplate(rdQueue);
	}

	@Bean
	public MessageListenerContainer messageListenerContainer(@Autowired Config config) {
		return new MessageListenerContainer(config);
	}

}
