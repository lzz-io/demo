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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.lzz.demo.jms.config.Constants;

/**
 * @author q1219331697
 *
 */
@Service
public class TopicConsumerByReceive {

	private static final Logger log = LoggerFactory.getLogger(TopicConsumerByReceive.class);

	public void doReceive() throws Exception {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://node3:61616");
		Connection connection = connectionFactory.createConnection("admin", "admin");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Destination destination = new ActiveMQTopic(Constants.TOPIC_TEST);
		Destination destination = session.createTopic(Constants.TOPIC_TEST);
		MessageConsumer consumer = session.createConsumer(destination);

		try {
			while (true) {
				Message message = consumer.receive();

				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();

				log.info(">>> get topic {}", text);
				log.info(Thread.currentThread().getName() + " -- " + Thread.currentThread().getId());
			}
		} catch (Exception e) {
			log.error("{}", e);
		} finally {
			consumer.close();
			session.close();
			connection.close();
		}

	}

}
