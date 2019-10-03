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

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 
 * qos限流
 * 
 * 
 * @author q1219331697
 *
 */
public class QosConsumer {

	private static final String exchange = "demo.qos.topic";
	private static final String routingKey = "demo.qos.topic.routingKey.#";
	private static final String queue = "demo.qos.topic.queue";

	public void receive() throws Exception {

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

		// qos限流
		// global 设置为 true的时候，并没有了限流的作用，有文章说是没有实现此功能
		channel.basicQos(0, 1, true);

		// 定义queue
		channel.queueDeclare(queue, true, false, false, null);

		// 绑定routingKey
		channel.queueBind(queue, exchange, routingKey);

		// 定义消费者
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {

				try {
					Thread.sleep(5000); // 模拟线程慢
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				String msg = new String(body);
				System.out.println("msg:" + msg + " getDeliveryTag:" + envelope.getDeliveryTag());

				// 手动ACK
				long deliveryTag = envelope.getDeliveryTag();
				channel.basicAck(deliveryTag, false);
			}
		};

		// 消费消息
		channel.basicConsume(queue, false, consumer);
	}

	public static void main(String[] args) throws Exception {
		new QosConsumer().receive();
	}

}
