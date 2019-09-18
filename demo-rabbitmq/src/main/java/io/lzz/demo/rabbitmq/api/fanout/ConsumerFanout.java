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

package io.lzz.demo.rabbitmq.api.fanout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @author q1219331697
 *
 */
public class ConsumerFanout {

	private static final Logger log = LoggerFactory.getLogger(ConsumerFanout.class);

	private static final String exchange = "demo.exchange.fanout";

	private static final String queue = "demo.exchange.fanout.queue";

	public void receiver() throws Exception {
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

		channel.queueDeclare(queue, true, false, false, null);

		channel.queueBind(queue, exchange, "");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, StandardCharsets.UTF_8);
				log.info("msg:{}", msg);

				long deliveryTag = envelope.getDeliveryTag();
				channel.basicAck(deliveryTag, false);
			}
		};

		channel.basicConsume(queue, false, consumer);
	}

	public static void main(String[] args) throws Exception {
		ConsumerFanout consumerFanout = new ConsumerFanout();
		consumerFanout.receiver();
	}
}
