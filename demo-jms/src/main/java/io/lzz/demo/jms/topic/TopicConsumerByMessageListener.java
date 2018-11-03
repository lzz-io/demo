/*
 * Copyright qq:1219331697
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lzz.demo.jms.topic;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import io.lzz.demo.jms.config.Constants;

/**
 * 1、创建消息监听容器@org.springframework.jms.listener.MessageListenerContainer
 * 2、实现@javax.jms.MessageListener接口
 * 
 * @author q1219331697
 *
 */
@Component
public class TopicConsumerByMessageListener implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(TopicConsumerByMessageListener.class);

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			log.info(">>> get topic {}:{}", text, getClass());
		} catch (JMSException e) {
			log.error("{}", e);
		}
	}

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
}
