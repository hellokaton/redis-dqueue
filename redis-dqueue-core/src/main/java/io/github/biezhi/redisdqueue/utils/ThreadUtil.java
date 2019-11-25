package io.github.biezhi.redisdqueue.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author biezhi
 * @date 2019/11/25
 */
@UtilityClass
public class ThreadUtil {

	private AtomicInteger jobThreadCounter = new AtomicInteger(1);

	private AtomicInteger callbackThreadCounter = new AtomicInteger(1);

	public ThreadFactory jobThreadFactory(int maxThreadSize) {
		return r -> {
			if (jobThreadCounter.get() == maxThreadSize) {
				jobThreadCounter.set(1);
			}
			Thread thread = new Thread(r, "delay-queue-job-" + jobThreadCounter.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		};
	}

	public ThreadFactory callbackThreadFactory(int maxThreadSize) {
		return r -> {
			if (callbackThreadCounter.get() == maxThreadSize) {
				callbackThreadCounter.set(1);
			}
			Thread thread = new Thread(r, "delay-queue-callback-" + callbackThreadCounter.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		};
	}

}
