package io.lzz.demo.jms.topic;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import io.lzz.demo.jms.config.Constants;

@Component
public class TopicMessageListenerContainer {

	@Autowired
	private ConnectionFactory connectionFactory;

	@Autowired
	private TopicConsumerByMessageListener topicConsumerByMessageListener;

	@Bean
	public MessageListenerContainer messageListenerContainer() {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setPubSubDomain(true);
		container.setDestinationName(Constants.TOPIC_TEST);
		container.setupMessageListener(topicConsumerByMessageListener);
		return container;
	}

}