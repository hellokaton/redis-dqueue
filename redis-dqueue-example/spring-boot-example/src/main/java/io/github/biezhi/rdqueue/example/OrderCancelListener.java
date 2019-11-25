package io.github.biezhi.rdqueue.example;

import io.github.biezhi.redisdqueue.enums.ConsumeStatus;
import io.github.biezhi.redisdqueue.spring.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author biezhi
 * @date 2019/11/22
 */
@Slf4j
@Component
public class OrderCancelListener implements MessageListener<String> {

	@Override
	public String topic() {
		return "order-cancel";
	}

	@Override
	public ConsumeStatus execute(String data) {
		log.info("取消订单: {}", data);
		return ConsumeStatus.CONSUMED;
	}

}
