package io.github.biezhi.redisdqueue.spring;

import io.github.biezhi.redisdqueue.core.Message;
import io.github.biezhi.redisdqueue.core.RDQueue;
import io.github.biezhi.redisdqueue.exception.RDQException;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * @author biezhi
 * @date 2019/11/21
 */
public class RDQueueTemplate {

    private RDQueue rdQueue;

    public RDQueueTemplate(RDQueue rdQueue) {
        this.rdQueue = rdQueue;
    }

    public <T extends Serializable> void syncPush(Message<T> message) throws RDQException {
        rdQueue.syncPush(message);
    }

    public <T extends Serializable> void syncPush(String key, Message<T> message) throws RDQException {
        rdQueue.syncPush(key, message);
    }

    public <T extends Serializable> void asyncPush(Message<T> message, BiConsumer<String, ? super Throwable> action) throws RDQException {
        rdQueue.asyncPush(message, action);
    }

    public <T extends Serializable> void asyncPush(String key, Message<T> message, BiConsumer<String, ? super Throwable> action) throws RDQException {
        rdQueue.asyncPush(key, message, action);
    }

}
