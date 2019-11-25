package io.github.biezhi.redisdqueue;

import io.github.biezhi.redisdqueue.configuration.Config;
import io.github.biezhi.redisdqueue.core.Callback;
import io.github.biezhi.redisdqueue.core.Message;
import io.github.biezhi.redisdqueue.core.RDQueue;
import io.github.biezhi.redisdqueue.enums.ConsumeStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author biezhi
 * @date 2019/11/21
 */
@Slf4j
public class TestMain {

	public static void main(String[] args) throws Exception {
		RDQueue rdQueue = new RDQueue(new Config());

		// 10 秒后发送 hello world 消息
		Message<String> message = new Message<>("TEST_TOPIC",
				"hello world", 10);

		// 发送延迟消息
		rdQueue.asyncPush(message, (key, throwable) -> log.info("key send ok:" + key));

		// 订阅消息
		rdQueue.subscribe("TEST_TOPIC", callback());

		System.in.read();
	}

	private static Callback<String> callback() {
		return new Callback<String>() {
			@Override
			public ConsumeStatus execute(String data) {
				log.info("消费数据:: {}", data);
				return ConsumeStatus.CONSUMED;
//			return ConsumeStatus.RETRY;
			}
		};
	}

}
