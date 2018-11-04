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
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.lzz.demo.jms.config.Constants;

/**
 * @author q1219331697
 *
 */
@RestController
public class TopicProducer {

	private static final Logger log = LoggerFactory.getLogger(TopicProducer.class);

	@Value(value = "${spring.activemq.broker-url}")
	private String brokerURL;

	@GetMapping("/topic")
	public String doSendTopic() throws Exception {

		log.info(brokerURL);
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Destination destination = new ActiveMQTopic(Constants.TOPIC_TEST);
		Destination destination = session.createTopic(Constants.TOPIC_TEST);
		MessageProducer producer = session.createProducer(destination);

		String text = Thread.currentThread().getName() + ":" + Thread.currentThread().getId();
		Message message = session.createTextMessage(text);

		producer.send(destination, message);
		log.info("<<< send topic {}", message);

		producer.close();
		session.close();
		connection.close();

		return "发送topic成功," + System.currentTimeMillis();
	}

}
