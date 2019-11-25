package io.github.biezhi.rdqueue.example;

import io.github.biezhi.redisdqueue.core.Message;
import io.github.biezhi.redisdqueue.exception.RDQException;
import io.github.biezhi.redisdqueue.spring.RDQueueTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

	@Autowired
	private RDQueueTemplate rdQueueTemplate;

	@GetMapping("/push")
	public String index(String id) throws RDQException {
		Message<String> message = new Message<>();
		message.setTopic("order-cancel");
		message.setPayload(id);
		message.setDelayTime(10);
		rdQueueTemplate.asyncPush(message, (s, throwable) -> {
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
