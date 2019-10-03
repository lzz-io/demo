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

import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * fanout: 广播
 * 
 * 1.这种模式不需要指定Routing key路由键，
 * 一个交换机可以绑定多个队列queue,一个queue可同时与多个exchange交换机进行绑定；
 * 
 * 2.如果消息发送到交换机上，但是这个交换机上面没有绑定的队列，那么这些消息将会被丢弃；
 * 
 * @author q1219331697
 *
 */
public class ProducerFanout {

	private static final String exchange = "demo.fanout";
	// fanout: 广播
	private static final String type = "fanout";

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

		channel.exchangeDeclare(exchange, type, true, false, null);

		// 发送消息
		String msg = "fanout msg! " + new Date();
		channel.basicPublish(exchange, "", null, msg.getBytes());

		// 非必须
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		ProducerFanout producer = new ProducerFanout();
		producer.send();
	}
}
