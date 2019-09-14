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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.UUID;

/**
 * @author q1219331697
 *
 */
public class JdbcInsertDataSimple {
	private int count = 0;

	long begin = 0;

	public void execute() {
		begin = System.currentTimeMillis();
		System.out.println("begin: " + begin);

		Connection connection = null;
		try {
			connection = DbUtils.getConnection();
			connection.setAutoCommit(false);

			String sql = "insert into tb_user (`username`, `password`, `update_time`) values(?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			int i = 0;
			while (i < 1000000) {
				preparedStatement.setString(1, UUID.randomUUID().toString().replace("-", ""));
				preparedStatement.setString(2, UUID.randomUUID().toString().replace("-", ""));
				preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
				preparedStatement.addBatch();

				count++;
				if (count % 100000 == 0) {
					preparedStatement.executeBatch();
					connection.commit();
					System.out.println("count:" + count);
					System.out.println(Thread.currentThread() + " time:" + (System.currentTimeMillis() - begin));
				}
				i++;
			}
			preparedStatement.executeBatch();
			connection.commit();
			System.out.println("count:" + count);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(connection);
			System.out.println(Thread.currentThread() + " time:" + (System.currentTimeMillis() - begin));
		}

	}

	public static void main(String[] args) {
		new JdbcInsertDataSimple().execute();
	}
}
