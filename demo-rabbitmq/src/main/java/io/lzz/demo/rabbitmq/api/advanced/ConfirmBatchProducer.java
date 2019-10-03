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

import java.util.Date;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * 批量confirm
 * 
 * spring-amqp不支持
 * 
 * @author q1219331697
 *
 */
public class ConfirmBatchProducer {

	private static final String exchange = "demo.confirm.topic";
	private static final String type = "topic";
	private static final String routingKey = "demo.confirm.topic.routingKey.batch";

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

		// 开启confirm
		channel.confirmSelect();

		// 消息持久化
		AMQP.BasicProperties props = new AMQP.BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8")
				.build();
		for (int i = 0; i < 5; i++) {
			// 发送消息到routingKey
			String msg = "confirm batch msg! " + new Date();
			channel.basicPublish(exchange, routingKey, props, msg.getBytes());
		}

		// 等待rabbitmq返回confirm，会阻塞线程
		// channel.waitForConfirmsOrDie(); //直到所有信息都发布，只要有一个未确认就会IOException
		if (channel.waitForConfirms()) {
			System.out.println("消息发送成功！");
		} else {
			System.out.println("消息发送失败！");
			// 补偿或者重发...
		}

		// 非必须
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		new ConfirmBatchProducer().send();
	}
}
