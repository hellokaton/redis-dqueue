package io.github.biezhi.rdqueue.example;

import io.github.biezhi.redisdqueue.enums.ConsumeStatus;
import io.github.biezhi.redisdqueue.spring.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPushListener implements MessageListener<String> {

    @Override
    public String topic() {
        return "order-push";
    }

    @Override
    public ConsumeStatus execute(String data) {
        log.info("推送订单: {}", data);
        return ConsumeStatus.CONSUMED;
    }
}
