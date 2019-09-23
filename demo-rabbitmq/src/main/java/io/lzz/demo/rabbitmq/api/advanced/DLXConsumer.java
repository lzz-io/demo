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
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 
 * DLX 死信队列 具体处理逻辑
 * 
 * 
 * @author q1219331697
 *
 */
public class DLXConsumer {

	private static final String exchange = "demo.dlx.dlx.topic";
	private static final String routingKey = "demo.dlx.biz.#";
	private static final String queue = "demo.dlx.biz.topic.queue";

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

		// 删除exchange，测试用
		// channel.queueDelete(queue);

		// 定义queue
		channel.queueDeclare(queue, true, false, false, null);

		// 绑定routingKey
		channel.queueBind(queue, exchange, routingKey);

		// 定义消费者
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body);
				System.out.println("msg:" + msg + " getDeliveryTag:" + envelope.getDeliveryTag() + " "
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));

				// 手动ACK
				long deliveryTag = envelope.getDeliveryTag();
				channel.basicAck(deliveryTag, false);
			}
		};

		// 消费消息
		channel.basicConsume(queue, false, consumer);
	}

	public static void main(String[] args) throws Exception {
		new DLXConsumer().receive();
	}

}
