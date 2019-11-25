package io.github.biezhi.redisdqueue.core;

import lombok.Data;

import java.io.Serializable;

/**
 * RawMessage
 *
 * @author biezhi
 * @date 2019/11/25
 */
@Data
public class RawMessage implements Serializable {

	private String key;
	private String topic;
	private String payload;

	private int maxRetries;
	private int hasRetries;

	private long executeTime;
	private long createTime;

	public void addHasRetries() {
		this.hasRetries++;
	}

}
