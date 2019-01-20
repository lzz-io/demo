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
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.lzz.demo.jms.config.Constants;
import io.lzz.demo.jms.entity.User;

/**
 * @author q1219331697
 *
 */
@Service
public class QueueConsumer {

	private static final Logger log = LoggerFactory.getLogger(QueueConsumer.class);

	private Long count = 1L;

	public void doRec() throws Exception {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://node3:61616");
		Connection connection = connectionFactory.createConnection("admin", "admin");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = session.createQueue(Constants.QUEUE_TEST);
		MessageConsumer consumer = session.createConsumer(destination);

		try {
			while (true) {
				Message message = consumer.receive();
				// double d = 10 / (count % 10);
				double d = (count % 10);
				long l = (long) d;
				if (count % 5 == 0) {
					Thread.sleep(200);
				}
				count++;
				log.info("count:{} d:{}  l:{}", count, d, l);

				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();

				ObjectMapper mapper = new ObjectMapper();
				User user = mapper.readValue(text, User.class);

				log.info(">>> get queue {}", user);
				log.info(Thread.currentThread().getName() + " -- " + Thread.currentThread().getId());
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			consumer.close();
			session.close();
			connection.close();
		}

	}

}
