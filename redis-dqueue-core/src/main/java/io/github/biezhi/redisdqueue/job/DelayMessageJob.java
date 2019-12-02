package io.github.biezhi.redisdqueue.job;

import io.github.biezhi.redisdqueue.configuration.Config;
import io.github.biezhi.redisdqueue.core.Callback;
import io.github.biezhi.redisdqueue.core.DQRedis;
import io.github.biezhi.redisdqueue.core.RawMessage;
import io.github.biezhi.redisdqueue.enums.ConsumeStatus;
import io.github.biezhi.redisdqueue.utils.ClassUtil;
import io.github.biezhi.redisdqueue.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * DelayMessageJob
 *
 * @author biezhi
 * @date 2019/11/21
 */
@Slf4j
public class DelayMessageJob extends BaseJob implements Runnable {

	private ExecutorService threadPool;

	private Map<String, Callback> callbacks;

	public DelayMessageJob(Config config, DQRedis dqRedis, ExecutorService threadPool) {
		super(config, dqRedis);
		this.threadPool = threadPool;
		this.callbacks = config.getCallbacks();
	}

	@Override
	public void run() {
		if (callbacks.isEmpty()) {
			return;
		}

		long now   = Instant.now().getEpochSecond();
		long begin = now - config.getTaskTtl();
		long end   = now - config.getCallbackTtl();

		try {
			List<String> keys = zrangebyscore(config.getDelayKey(), begin, end);
			if (null == keys || keys.isEmpty()) {
				return;
			}

			keys.stream()
					.filter(config::waitProcessing)
					.map(key -> (Runnable) () -> handleCallback(key))
					.forEach(threadPool::submit);
		} catch (Exception e) {
			log.error("zrangebyscore({}, {}-{})", config.getDelayKey(), begin, now, e);
		}
	}

	private <T extends Serializable> void handleCallback(final String key) {
		config.addProcessed(key);
		RawMessage rawMessage = getTask(key);
		if (null == rawMessage) {
			return;
		}
		if (!callbacks.containsKey(rawMessage.getTopic())) {
			return;
		}
		long score = Instant.now().getEpochSecond();

		// will delay message dump to wait for an ack confirmed
		// that only allows a consumer operating at this time
		if (!this.transferMessage(key, config.getDelayKey(), config.getAckKey(), score)) {
			config.processed(key);
			return;
		}

		Callback callback = callbacks.get(rawMessage.getTopic());

		Class<T> type = ClassUtil.getGenericType(callback);

		T payload;
		if (ClassUtil.isBasicType(type)) {
			payload = (T) ClassUtil.convert(type, rawMessage.getPayload());
		} else {
			payload = GsonUtil.fromJson(rawMessage.getPayload(), type);
		}

		// when the confirmed result to retry, configure a retry sending the limited time
		if (ConsumeStatus.RETRY.equals(callback.execute(payload))) {
			this.retry(key, rawMessage);
		} else {
			// consumption is successful, delete the message
			this.deleteMessage(key);
		}
		config.processed(key);
	}

	private void retry(String key, RawMessage rawMessage) {
		rawMessage.addHasRetries();
		if (rawMessage.getHasRetries() > rawMessage.getMaxRetries()) {
			this.deleteMessage(key);
			return;
		}
		redis.hset(config.getHashKey(), key, GsonUtil.toJson(rawMessage));

		// (1,2,4,8...) * X
		long now   = Instant.now().getEpochSecond();
		int  adder = (int) Math.pow(2, rawMessage.getHasRetries() - 1) * config.getRetryInterval();
		long score = now + adder;
		transferMessage(key, config.getAckKey(), config.getErrorKey(), score);
	}

}
