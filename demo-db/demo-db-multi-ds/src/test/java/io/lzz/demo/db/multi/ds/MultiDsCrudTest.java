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

package io.lzz.demo.db.multi.ds;

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

import io.lzz.demo.db.multi.ds.primary.entity.User;
import io.lzz.demo.db.multi.ds.primary.entity.UserInfo;
import io.lzz.demo.db.multi.ds.secondary.entity.Order;
import io.lzz.demo.db.multi.ds.secondary.entity.OrderInfo;
import io.lzz.demo.db.multi.ds.service.OrderInfoService;
import io.lzz.demo.db.multi.ds.service.OrderService;
import io.lzz.demo.db.multi.ds.service.UserInfoService;
import io.lzz.demo.db.multi.ds.service.UserService;

/**
 * @author q1219331697
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultiDsCrudTest {

	private static final Logger log = LoggerFactory.getLogger(MultiDsCrudTest.class);

	@Autowired
	private UserService userService;
	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderInfoService orderInfoService;

	@Test
	public void testCrud() {
		log.info("testCrud()");

		for (int i = 0; i < 5; i++) {
			User user = userService.save(new User(null, "username", "password"));
			userInfoService.save(new UserInfo(null, user.getId(), "user info"));

			List<User> users = userService.findAll();
			log.info("{}", users);
			log.info("{}", userInfoService.findAll());

			Order order = orderService.save(new Order(null, user.getId(), "order remark"));
			orderInfoService.save(new OrderInfo(null, order.getId(), "order info"));

			List<Order> orders = orderService.findAll();
			log.info("{}", orders);
			log.info("{}", orderInfoService.findAll());

		}

		log.info("testCrud end ------");
	}

}
