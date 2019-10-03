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

package io.lzz.demo.rabbitmq.api.direct;

import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * direct: 直连
 * 
 * 
 * 1.任何发送到Direct Exchange的消息都会被转发到指定RouteKey中指定的队列Queue；
 * 
 * 2.生产者生产消息的时候需要执行Routing Key路由键；
 * 
 * 3.队列绑定交换机的时候需要指定Binding Key,只有路由键与绑定键相同的话，
 * 才能将消息发送到绑定这个队列的消费者；
 * 
 * 4.如果vhost中不存在RouteKey中指定的队列名，则该消息会被丢弃；
 * 
 * 
 * @author q1219331697
 *
 */
public class ProducerDirect {

	private static final String exchange = "demo.direct";
	// direct: 直连
	private static final String type = "direct";
	private static final String routingKey = "demo.direct.routingKey";

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

		// 定义交换机
		channel.exchangeDeclare(exchange, type, true, false, null);

		// 发送消息
		String msg = "direct msg! " + new Date();
		channel.basicPublish(exchange, routingKey, null, msg.getBytes());

		// 非必须
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		ProducerDirect producer = new ProducerDirect();
		producer.send();
	}
}
