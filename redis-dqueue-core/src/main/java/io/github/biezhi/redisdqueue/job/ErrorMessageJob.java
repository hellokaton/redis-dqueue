package io.github.biezhi.redisdqueue.job;

import io.github.biezhi.redisdqueue.configuration.Config;
import io.github.biezhi.redisdqueue.core.DQRedis;
import io.lettuce.core.Range;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

/**
 * ErrorMessageJob
 *
 * @author biezhi
 * @date 2019/11/21
 */
@Slf4j
public class ErrorMessageJob extends BaseJob implements Runnable {

	public ErrorMessageJob(Config config, DQRedis redis) {
		super(config, redis);
	}

	@Override
	public void run() {
		long now   = Instant.now().getEpochSecond();
		long begin = now - config.getTaskTtl();

		List<String> keys = zrangebyscore(config.getErrorKey(), begin, now);
		if (null == keys || keys.isEmpty()) {
			return;
		}

		for (String key : keys) {
			transferMessage(key, config.getErrorKey(), config.getDelayKey(), Instant.now().getEpochSecond());
		}
	}

}
