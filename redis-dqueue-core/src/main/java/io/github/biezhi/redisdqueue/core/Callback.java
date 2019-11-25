package io.github.biezhi.redisdqueue.core;

import io.github.biezhi.redisdqueue.enums.ConsumeStatus;

/**
 * Callback
 *
 * @author biezhi
 * @date 2019/11/21
 */
public interface Callback<T> {

	ConsumeStatus execute(T data);

}
