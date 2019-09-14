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

package io.lzz.demo.db.mysql.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author q1219331697
 *
 */
public class DbUtils {

	private static final Logger log = LoggerFactory.getLogger(DbUtils.class);

	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

	public static Connection getConnection() throws Exception {
		Connection connection = threadLocal.get();
		if (null == connection || connection.isClosed()) {

			// Properties
			Properties properties = new Properties();
			properties.load(DbUtils.class.getClassLoader().getResourceAsStream("application.yml"));
			String url = properties.getProperty("url");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");

			// HikariPool
			// HikariConfig hikariConfig = new HikariConfig();
			// hikariConfig.setJdbcUrl(url);
			// hikariConfig.setUsername(username);
			// hikariConfig.setPassword(password);
			// hikariConfig.setMaximumPoolSize(500);
			// hikariConfig.setMinimumIdle(20);
			// hikariConfig.setConnectionTimeout(0);
			// hikariConfig.setIdleTimeout(100000);
			//
			// HikariPool hikariPool = new HikariPool(hikariConfig);
			// connection = hikariPool.getConnection();

			// jdbc DriverManager
			connection = DriverManager.getConnection(url, username, password);
			threadLocal.set(connection);
			log.info("new mysql connection:{}", connection);
			return connection;
		}
		log.info("cache mysql connection:{}", connection);
		return connection;
	}

	public static void close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		threadLocal.remove();
	}
}
