package io.github.biezhi.redisdqueue.spring;

import io.github.biezhi.redisdqueue.core.Callback;

/**
 * MessageListener
 *
 * @author biezhi
 * @date 2019/11/22
 */
public interface MessageListener<T> extends Callback<T> {

	String topic();

}
