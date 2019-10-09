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

package io.lzz.demo.jms.api;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author q1219331697
 *
 */
public class MqConnectionFactory {

	private static GenericObjectPool<Connection> pool;

	static {
		GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
		config.setMaxTotal(4);
		pool = new GenericObjectPool<>(new CustomPooledObjectFactory(), config);
	}

	private MqConnectionFactory() {
	}

	public static Connection getConnection() {
		try {
			return pool.borrowObject();
		} catch (Exception e) {
		}
		return null;
	}

	public static void closeConnection(Connection connection) {
		pool.returnObject(connection);
	}

	private static class CustomPooledObjectFactory extends BasePooledObjectFactory<Connection> {

		@Override
		public Connection create() throws Exception {
			String brokerURL = "tcp://127.0.0.1:61616";
			String userName = "admin";
			String password = "admin";
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);
			Connection connection = connectionFactory.createConnection();
			connection.start();
			return connection;
		}

		@Override
		public PooledObject<Connection> wrap(Connection obj) {
			return new DefaultPooledObject<>(obj);
		}

	}
}
