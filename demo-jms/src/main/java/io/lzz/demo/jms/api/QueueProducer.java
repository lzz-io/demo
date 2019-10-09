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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import io.lzz.demo.jms.config.Constants;

/**
 * @author q1219331697
 *
 */
public class QueueProducer {

	public void send(String text) {
		Connection connection = MqConnectionFactory.getConnection();
		Session session = null;

		try {
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(Constants.QUEUE_TEST);
			MessageProducer producer = session.createProducer(queue);
			 // 设置持久化模式
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            
			Message message = session.createTextMessage(text);
			producer.send(message);

			Thread.sleep(50);
			session.commit();
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

	public static void main(String[] args) {

		for (int i = 0; i < 1000; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
					new QueueProducer().send(text);
				}
			}).start();
		}
	}
}
