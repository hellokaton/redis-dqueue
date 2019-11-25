# redis-dqueue

`redis-dqueue` is a `redis` + `Java 8` base delayed queue library.

## Feature

- Push message delay
- Consumer allowed to try again
- Based on the message separation of the topic
- Integrated SpringBoot

## Normal Java Application

With Maven

```xml
<dependency>
    <groupId>io.github.biezhi</groupId>
    <artifactId>redis-dqueue-core</artifactId>
    <version>[LATEST_VERSION]</version>
</dependency>
```

### Push message and subscribe topic

```java
RDQueue rdQueue = new RDQueue(new Config());

// "hello world" messages sent after 10 seconds
Message<String> message = new Message<>("TEST_TOPIC", "hello world", 10);

// async push delay message
rdQueue.asyncPush(message, (key, throwable) -> log.info("key send ok:" + key));

// subscribe topic
rdQueue.subscribe("TEST_TOPIC", callback());
```

## Spring Boot Application

With Maven

```xml
<dependency>
    <groupId>io.github.biezhi</groupId>
    <artifactId>redis-dqueue-core</artifactId>
    <version>[LATEST_VERSION]</version>
</dependency>
```

### Push message

```java
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
        }
    });
    return "推送成功";
}
```

### Subscribe topic

You need to implement `MessageListener`, subscribe to the related topic, process delay messages in the execute method.

> Ensure that the class was Spring managed.

```java
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
```

## Lisence

[Apache2](LISENCE)
