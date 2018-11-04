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

package io.lzz.demo.jms.queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lzz.demo.jms.config.Constants;
import io.lzz.demo.jms.entity.User;

/**
 * @author q1219331697
 *
 */
@RestController
public class QueueProducer {

	private static final Logger log = LoggerFactory.getLogger(QueueProducer.class);

	@GetMapping("/queue")
	public String doSendQueue() throws Exception {

		Thread currentThread = Thread.currentThread();
		User user = new User(currentThread.getId(), currentThread.getName());
		ObjectMapper mapper = new ObjectMapper();
		String userString = null;
		try {
			userString = mapper.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			log.error("{}", e);
		}

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://node3:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = new ActiveMQQueue(Constants.QUEUE_TEST);
		MessageProducer producer = session.createProducer(destination);
		Message message = session.createTextMessage(userString);

		producer.send(destination, message);
		log.info("<<< send queue {}", userString);

		producer.close();
		session.close();
		connection.close();

		return "发送queue成功," + System.currentTimeMillis();
	}

}
