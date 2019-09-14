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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author q1219331697
 *
 */
public class UserDaoTest {

	private static final Logger log = LoggerFactory.getLogger(UserDaoTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetConnection() {
		UserDao userDao = new UserDao();
		Connection connection = userDao.getConnection();
		log.info("{}", connection);
	}

	@Test
	public void testSelect() throws Exception {
		UserDao userDao = new UserDao();
		userDao.select();
	}

	@Test
	public void testInsert() throws Exception {
		UserDao userDao = new UserDao();
		int i = userDao.insert();
		log.info("影响行数:{}", i);
	}

	@Test
	public void testUpdate() throws Exception {
		UserDao userDao = new UserDao();
		int i = userDao.update();
		log.info("影响行数:{}", i);
	}

	@Test
	public void testDelete() throws Exception {
		UserDao userDao = new UserDao();
		int i = userDao.delete();
		log.info("影响行数:{}", i);
	}

}
