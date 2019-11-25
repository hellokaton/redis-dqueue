package io.github.biezhi.redisdqueue.spring;

import io.github.biezhi.redisdqueue.core.Callback;
import io.github.biezhi.redisdqueue.configuration.Config;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

/**
 * MessageListenerContainer
 *
 * @author biezhi
 * @date 2019/11/22
 */
public class MessageListenerContainer implements BeanPostProcessor {

	private Map<String, Callback> callbacks;

	public MessageListenerContainer(Config config) {
		this.callbacks = config.getCallbacks();
	}

	@Override
	public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
		if (o instanceof MessageListener) {
			MessageListener messageListener = (MessageListener) o;
			callbacks.put(messageListener.topic(), messageListener::execute);
		}
		return o;
	}

}
