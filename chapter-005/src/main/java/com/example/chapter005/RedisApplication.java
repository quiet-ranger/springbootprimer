package com.example.chapter005;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.example.payroll.Employee;
import com.example.receivers.Receiver;

@SpringBootApplication
public class RedisApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisApplication.class);

	@Bean
	RedisMessageListenerContainer container(
		RedisConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter
	) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("chat"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "consume");
	}

	@Bean
	Receiver receiver() {
		return new Receiver();
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = SpringApplication.run(RedisApplication.class, args);

		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
		Receiver receiver = ctx.getBean(Receiver.class);

		// Create a sample POJO, our entity
		Employee emp = new Employee("Joe Bloggs", "UX Designer");
		emp.setId(1L);
		
		ObjectMapper mapper = new ObjectMapper();
		LOGGER.info("Sending message...");
		try {
			template.convertAndSend("chat", mapper.writeValueAsString(emp));
			while (receiver.getCount() < 5) {
				Thread.sleep(500L);
		    }
		} catch (JsonProcessingException e) {
			System.err.println("Failed to convert to JSON");
		}

		System.exit(0);
	}
}