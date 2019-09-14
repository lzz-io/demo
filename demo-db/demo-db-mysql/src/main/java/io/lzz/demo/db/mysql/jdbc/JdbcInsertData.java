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
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.DigestUtils;

/**
 * @author q1219331697
 *
 */
public class JdbcInsertData {

	private static final int TOTAL = 1000000;
	private static final int BATCH = 10000;
	private AtomicInteger counter = new AtomicInteger(0);

	long begin = 0;

	public void execute() {
		begin = System.currentTimeMillis();
		System.out.println("begin: " + begin);

		ExecutorService threadPool = Executors.newFixedThreadPool(500);
		for (int i = 0; i < 2000; i++) {
			threadPool.submit(new InsertData());
		}

		threadPool.shutdown();
	}

	class InsertData implements Runnable {

		public InsertData() {
		}

		@Override
		public void run() {
			// System.err.println("i.hashCode():" + i.hashCode());
			System.err.println("counter.hashCode():" + counter.hashCode());

			Connection connection = null;
			try {
				connection = DbUtils.getConnection();
				connection.setAutoCommit(false);

				String sql = "insert into tb_user (`username`, `password`, `update_time`) values(?, ?, ?)";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);

				int id = 0;
				int i = 0;
				while (true) {
					id = counter.incrementAndGet();
					if (id > TOTAL) {
						break;
					}

					// System.out.println(Thread.currentThread() + " 1.counter : " + id);
					preparedStatement.setString(1, "username" + id);
					preparedStatement.setString(2, DigestUtils.md5DigestAsHex(("password" + id).getBytes()));
					preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					preparedStatement.addBatch();
					i++;

					if (i % BATCH == 0) {
						preparedStatement.executeBatch();
						connection.commit();
						System.out.println(Thread.currentThread() + " 2. i:" + i);
						System.out.println(Thread.currentThread() + " 2. count:" + id);
						System.out.println(Thread.currentThread() + " 2. time:" + (System.currentTimeMillis() - begin));
					}
				}

				preparedStatement.executeBatch();
				connection.commit();
				System.out.println(Thread.currentThread() + " 3. i:" + i);
				System.out.println(Thread.currentThread() + " 3. count:" + id);
				System.out.println(Thread.currentThread() + " 3. time:" + (System.currentTimeMillis() - begin));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtils.close(connection);
			}
		}
	}

	public static void main(String[] args) {
		new JdbcInsertData().execute();
	}
}
