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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * table tb_user crud 例子
 * 
 * @author q1219331697
 *
 */
public class UserDao {

	private static final Logger log = LoggerFactory.getLogger(UserDao.class);

	public Connection getConnection() {
		// Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/jdbc_test";
		String user = "root";
		String password = "123456";
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public void select() throws Exception {
		Connection connection = getConnection();
		String sql = "select * from tb_user limit 0, 10";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		ResultSet resultSet = prepareStatement.executeQuery();

		while (resultSet.next()) {
			log.info("id:{}", resultSet.getInt("id"));
			log.info("username:{}", resultSet.getString("username"));
			log.info("password:{}", resultSet.getObject("password"));
			log.info("update_time:{}", resultSet.getTimestamp("update_time"));
		}

	}

	public int insert() throws Exception {
		Connection connection = getConnection();
		String sql = "INSERT INTO `tb_user` (`id`, `username`, `password`, `update_time`) VALUES (?, ?, ?, ?)";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);

		prepareStatement.setInt(1, 10000001);
		prepareStatement.setString(2, "user1");
		prepareStatement.setString(3, "pwd");
		prepareStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

		// prepareStatement.setObject(1, 10000003);
		// prepareStatement.setObject(2, "user1");
		// prepareStatement.setObject(3, "pwd");
		// prepareStatement.setObject(4, new Date());

		return prepareStatement.executeUpdate();
	}

	public int update() throws Exception {
		Connection connection = getConnection();
		String sql = "UPDATE tb_user SET update_time=? WHERE id = ?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);

		prepareStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		prepareStatement.setInt(2, 10000001);

		return prepareStatement.executeUpdate();
	}

	public int delete() throws Exception {
		Connection connection = getConnection();
		String sql = "DELETE FROM tb_user WHERE id = ?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setInt(1, 10000001);

		return prepareStatement.executeUpdate();
	}

}
