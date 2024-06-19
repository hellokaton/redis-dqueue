package io.github.biezhi.rdqueue.example;

import io.github.biezhi.redisdqueue.core.Message;
import io.github.biezhi.redisdqueue.core.RawMessage;
import io.github.biezhi.redisdqueue.exception.RDQException;
import io.github.biezhi.redisdqueue.spring.RDQueueTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;

@RestController
@SpringBootApplication
public class Application {

	@Autowired
	private RDQueueTemplate rdQueueTemplate;

	@GetMapping("/push")
	public String index(String id) throws RDQException {
		Double round = Math.random();
		Message<String> message = new Message<>();
		message.setTopic("order-cancel");
		message.setPayload(String.valueOf(round));
		message.setDelayTime(10);
		String key = "111";
		rdQueueTemplate.asyncPush(key, message, (s, throwable) -> {
			if (null != throwable) {
				throwable.printStackTrace();
			} else {
				System.out.println("s" + s);
			}
		});

		Message<String> messagePush = new Message<>();
		messagePush.setTopic("order-push");
		messagePush.setPayload(String.valueOf(round));
		messagePush.setDelayTime(1);
		rdQueueTemplate.asyncPush(messagePush, (s, throwable) -> {
			if (null != throwable) {
				throwable.printStackTrace();
			} else {
				System.out.println("s" + s);
			}
		});


		return "推送成功";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
