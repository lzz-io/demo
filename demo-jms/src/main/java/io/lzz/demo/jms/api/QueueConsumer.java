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

package io.lzz.demo.jms.api;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import io.lzz.demo.jms.config.Constants;

/**
 * @author q1219331697
 *
 */
public class QueueConsumer {

	public void receive() {
		while (true) {
			Connection connection = MqConnectionFactory.getConnection();
			Session session = null;

			try {
				session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
				Queue queue = session.createQueue(Constants.QUEUE_TEST);
				MessageConsumer consumer = session.createConsumer(queue);
				// Message message = consumer.receive(1000);
				Message message = consumer.receive();

				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();

				System.out.println(text);
				
				message.acknowledge();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					session.close();
					MqConnectionFactory.closeConnection(connection);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		new QueueConsumer().receive();
	}
}
