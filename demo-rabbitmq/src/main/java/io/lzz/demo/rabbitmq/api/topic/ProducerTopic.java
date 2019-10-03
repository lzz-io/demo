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

package io.lzz.demo.rabbitmq.api.topic;

import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * topic: 通配符
 * 
 * 
 * 1.任何发送到Topic Exchange的消息都会被
 * 转发到所有满足Route Key与Binding Key模糊匹配的队列Queue上；
 * 
 * 2.生产者发送消息的时候需要指定Route Key，
 * 同时绑定Exchange与Queue的时候也需要指定Binding Key；
 * 
 * 3.#” 表示0个或多个关键字，“*”表示匹配一个关键字
 * 
 * 4.如果Exchange没有发现能够与RouteKey模糊匹配的队列Queue，则会抛弃此消息；
 * 
 * 5.如果Binding中的Routing key *，#都没有，
 * 则路由键跟绑定键相等的时候才转发消息，类似Direct Exchange；
 * 如果Binding中的Routing key为#或者#.#，则全部转发，类似Fanout Exchange；
 * 
 * 
 * @author q1219331697
 *
 */
public class ProducerTopic {

	private static final String exchange = "demo.topic";
	// topic: 通配符
	private static final String type = "topic";
	private static final String routingKey = "demo.topic.routingKey.user.#";

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
		String msg = "topic msg! " + new Date();
		channel.basicPublish(exchange, routingKey, null, msg.getBytes());

		// 非必须
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		ProducerTopic producer = new ProducerTopic();
		producer.send();
	}
}
