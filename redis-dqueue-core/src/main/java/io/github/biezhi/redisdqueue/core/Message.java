package io.github.biezhi.redisdqueue.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Delay Queue Message
 *
 * @author biezhi
 * @date 2019/11/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message<T extends Serializable> implements Serializable {

	private String topic;

	private T payload;

	private int retries = 3;

	private transient int delayTime;

	private transient TimeUnit timeUnit = TimeUnit.SECONDS;

	public Message(String topic, T payload, int delayTime) {
		this.topic = topic;
		this.payload = payload;
		this.delayTime = delayTime;
	}

	@Override
	public String toString() {
		return "(payload=" + payload +
				", topic=" + topic +
				", retries=" + retries +
				", delayTime=" + timeUnit.toSeconds(delayTime) +
				"seconds)";
	}

}
