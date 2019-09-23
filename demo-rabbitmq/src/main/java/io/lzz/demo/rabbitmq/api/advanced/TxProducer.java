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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * rabbitmq 事务
 * 
 * 一般不用，性能太差
 * 
 * 
 * @author q1219331697
 *
 */
public class TxProducer {

	private static final String exchange = "demo.tx.direct";
	private static final String type = "direct";
	private static final String routingKey = "demo.tx.direct.routingKey";

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

		// 开启事务
		channel.txSelect();

		// 发送消息到routingKey
		String msg = "tx msg! " + new Date();
		channel.basicPublish(exchange, routingKey, null, msg.getBytes());

		// 提交事务
		channel.txCommit();

		// 如果异常，决定重发或记录日志还是回滚
		// 回滚事务
		// channel.txRollback();

		// 非必须
		channel.close();
		connection.close();
	}

	public static void main(String[] args) throws Exception {
		new TxProducer().send();
	}
}
