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

package io.lzz.demo.db.mysql;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.lzz.demo.db.mysql.dao.UserDao;
import io.lzz.demo.db.mysql.entity.User;

/**
 * @author q1219331697
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MysqlCrudTest {

	private static final Logger log = LoggerFactory.getLogger(MysqlCrudTest.class);

	@Autowired
	private UserDao userDao;

	@Test
	public void testCrud() {
		log.info("testCrud()");

		for (int i = 0; i < 5; i++) {
			int num = userDao.save("insert into tb_user(username, password) values('username','password')");
			log.info("{}", num);

			List<User> users = userDao.findAll();
			log.info("{}", users);
		}

		log.info("testCrud end ------");
	}

}
