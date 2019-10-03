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

package io.lzz.demo.rabbitmq.api.advanced;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * qos限流
 * 
 * @author q1219331697
 *
 */
public class QosProducer {

	private static final String exchange = "demo.qos.topic";
	private static final String type = "topic";
	private static final String routingKey = "demo.qos.topic.routingKey";

	public void send() throws Exception {

		ConnectionFactory connectionFactory = new ConnectionFactory();
		String host = "localhost";
		String username = "guest";
		String password = "guest";
		int port = 5672;
		connectionFactory.setHost(host);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setPort(port);
		Connection connection = connectionFactory.newConnection();

		Channel channel = connection.createChannel();

		// 定义exchange
		channel.exchangeDeclare(exchange, type, true, false, null);

		// 消息持久化
		AMQP.BasicProperties props = new AMQP.BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8")
				.build();
		for (int i = 0; i < 50; i++) {
			// 发送消息到routingKey
			String msg = "qos msg! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
			channel.basicPublish(exchange, routingKey, props, msg.getBytes());
		}

		// 非必须
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		new QosProducer().send();
	}
}
