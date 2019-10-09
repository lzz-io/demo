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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.lzz.demo.jms.config.Constants;

/**
 * 
 * 1.消费者必须先启动
 * 
 * @author q1219331697
 *
 */
public class TopicProducer {

	public void send(String text) {
		String brokerURL = "tcp://127.0.0.1:61616";
		String userName = "admin";
		String password = "admin";
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);

		Connection connection = null;
		Session session = null;

		try {
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(Constants.TOPIC_TEST);
			MessageProducer producer = session.createProducer(topic);
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
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		ExecutorService threadPool = Executors.newFixedThreadPool(10);

		for (int i = 0; i < 100; i++) {
			threadPool.submit(() -> {
				String text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
				new TopicProducer().send(text);
			});
		}

		threadPool.shutdown();

		System.out.println("send finish");
	}
}
