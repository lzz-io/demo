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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * listener confirm
 * 
 * @author q1219331697
 *
 */
public class ConfirmListenerProducer {

	private static final Logger log = LoggerFactory.getLogger(ConfirmListenerProducer.class);

	private static final String exchange = "demo.confirm.topic";
	private static final String type = "topic";
	private static final String routingKey = "demo.confirm.topic.routingKey.listener";

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

		// 将channel设置为confirm模式
		channel.confirmSelect();

		// 发送消息到routingKey
		String msg = "confirm single msg! " + new Date();
		// 消息持久化
		AMQP.BasicProperties props = new AMQP.BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8")
				.build();
		channel.basicPublish(exchange, routingKey, props, msg.getBytes());

		// 添加listener，异步
		channel.addConfirmListener(new ConfirmListener() {

			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				log.info("Nack! deliveryTag:{}, multiple:{}", deliveryTag, multiple);
			}

			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				log.info("Ack! deliveryTag:{}, multiple:{}", deliveryTag, multiple);
			}
		});

		// 非必须
		Thread.sleep(1000);// 等待接收到rabbitmq的返回信息
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		new ConfirmListenerProducer().send();
	}
}
