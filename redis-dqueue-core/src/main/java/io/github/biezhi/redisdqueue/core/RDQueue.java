package io.github.biezhi.redisdqueue.core;

import io.github.biezhi.redisdqueue.configuration.Config;
import io.github.biezhi.redisdqueue.constans.LuaScriptConst;
import io.github.biezhi.redisdqueue.exception.RDQException;
import io.github.biezhi.redisdqueue.job.AckMessageJob;
import io.github.biezhi.redisdqueue.job.DelayMessageJob;
import io.github.biezhi.redisdqueue.job.ErrorMessageJob;
import io.github.biezhi.redisdqueue.utils.ClassUtil;
import io.github.biezhi.redisdqueue.utils.GsonUtil;
import io.github.biezhi.redisdqueue.utils.ThreadUtil;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScriptOutputType;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * RDQueue
 *
 * @author biezhi
 * @date 2019/11/21
 */
@Slf4j
public class RDQueue {

	private DQRedis dqRedis;

	private final Config config;

	public RDQueue(Config config) {
		this.config = config;
		this.init();
	}

	private void init() {
		this.dqRedis = null;
		if (config.isHasPassword()) {
			RedisURI redisURI = new RedisURI();
			redisURI.setHost(config.getHost());
			redisURI.setPort(config.getPort());
			redisURI.setPassword(config.getPassword());
			this.dqRedis = new DQRedis(redisURI, config.getCluster());
		} else {
			this.dqRedis = new DQRedis(config.getRedisURI(), config.getCluster());
		}

		log.info("redis-dqueue starting...");

		int maxJobCoreSize      = config.getMaxJobCoreSize();
		int maxCallbackCoreSize = config.getMaxCallbackCoreSize();

		ScheduledExecutorService jobThreadPool =
				new ScheduledThreadPoolExecutor(maxJobCoreSize, ThreadUtil.jobThreadFactory(maxJobCoreSize));

		ExecutorService callbackThreadPool =
				Executors.newFixedThreadPool(maxCallbackCoreSize, ThreadUtil.callbackThreadFactory(maxCallbackCoreSize));

		jobThreadPool.scheduleAtFixedRate(new DelayMessageJob(config, dqRedis, callbackThreadPool), 0, 200, TimeUnit.MILLISECONDS);
		jobThreadPool.scheduleAtFixedRate(new AckMessageJob(config, dqRedis), 0, 1000, TimeUnit.MILLISECONDS);
		jobThreadPool.scheduleAtFixedRate(new ErrorMessageJob(config, dqRedis), 0, 1000, TimeUnit.MILLISECONDS);
	}

	public void asyncPush(Message<?> message, BiConsumer<String, ? super Throwable> action) throws RDQException {
		this.push(message, action, true);
	}

	public void asyncPush(String key, Message<?> message, BiConsumer<String, ? super Throwable> action) throws RDQException {
		this.push(key, message, action, true);
	}

	private void push(String key, Message<?> message, BiConsumer<String, ? super Throwable> action, boolean asyncExecute) throws RDQException {
		this.checkQueue(config.getKeyPrefix(), message);

		String queueKey = config.getDelayKey();
		log.info("push message {}", message);

		RawMessage rawMessage = buildTask(key, message);
		String     hashValue  = GsonUtil.toJson(rawMessage);

		String[] keys = new String[]{config.getHashKey(), queueKey, key};
		String[] args = new String[]{hashValue, rawMessage.getExecuteTime() + ""};

		if (asyncExecute) {
			dqRedis.asyncEval(LuaScriptConst.PUSH_MESSAGE, ScriptOutputType.INTEGER, keys, args)
					.thenApply(NULL -> key)
					.whenComplete(action);
		} else {
			Long result = dqRedis.syncEval(LuaScriptConst.PUSH_MESSAGE, ScriptOutputType.INTEGER, keys, args);
			log.debug("sync push result: {}", result);
		}
	}

	public void syncPush(Message<?> message) throws RDQException {
		this.push(message, null, false);
	}

	public void syncPush(String key, Message<?> message) throws RDQException {
		this.push(key, message, null, false);
	}

	private void push(Message<?> message, BiConsumer<String, ? super Throwable> action, boolean asyncExecute) throws RDQException {
		String key = UUID.randomUUID().toString().replace("-", "");
		push(key, message, action, asyncExecute);
	}

	private RawMessage buildTask(String key, Message<?> message) {
		long delayTime = message.getTimeUnit().toSeconds(message.getDelayTime());

		long now = Instant.now().getEpochSecond();

		long executeTime = now + delayTime;

		RawMessage rawMessage = new RawMessage();
		rawMessage.setKey(key);
		rawMessage.setCreateTime(now);
		rawMessage.setExecuteTime(executeTime);
		rawMessage.setTopic(message.getTopic());
		Class<? extends Serializable> type = message.getPayload().getClass();
		if (ClassUtil.isBasicType(type)) {
			rawMessage.setPayload(message.getPayload().toString());
		} else {
			rawMessage.setPayload(GsonUtil.toJson(message.getPayload()));
		}
		rawMessage.setMaxRetries(message.getRetries());
		rawMessage.setHasRetries(0);
		return rawMessage;
	}

	private void checkQueue(String queueName, Message<?> message) throws RDQException {
		if (null == queueName || queueName.isEmpty()) {
			throw new RDQException("queue name can not be empty.");
		}

		if (null == message) {
			throw new RDQException("message can not be null.");
		}

		if (null == message.getPayload()) {
			throw new RDQException("message payload can not be null.");
		}
	}

	public void subscribe(String topic, Callback<?> callback) {
		log.info("listen the topic [{}]", topic);
		config.getCallbacks().put(topic, callback);
	}

	public void shutdown() {
		dqRedis.shutdown();
	}

}
