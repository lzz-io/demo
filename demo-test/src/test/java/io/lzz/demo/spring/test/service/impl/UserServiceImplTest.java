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

package io.lzz.demo.spring.test.service.impl;

import io.lzz.demo.spring.test.entity.User;
import io.lzz.demo.spring.test.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author q1219331697
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImplTest.class);

	@Autowired
	// @InjectMocks
	// @MockBean
	private UserService userService;
	//= Mockito.mock(UserServiceImpl.class);

	// @Mock
	// @Autowired
	// private UserRepository userRepository;

	@Before
	public void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for
	 * {@link io.lzz.demo.spring.test.service.impl.UserServiceImpl#findAll()}.
	 */
	@Test
	public void testFindAll() {
		List<User> users = userService.findAll();
		log.info("{}", users.size());
		// Assert.assertNotNull(users);
	}

	/**
	 * Test method for
	 * {@link io.lzz.demo.spring.test.service.impl.UserServiceImpl#findById(java.lang.Integer)}.
	 */
	@Test
	public void testFindById() {
	}

	/**
	 * Test method for
	 * {@link io.lzz.demo.spring.test.service.impl.UserServiceImpl#save(io.lzz.demo.spring.test.entity.User)}.
	 */
	@Test
	public void testSave() {
		// User user = new User(null, "username1", new Date());
		User user = new User();
		user.setUsername("username1");
		user.setCreateTime(new Date());
		User save = userService.save(user);
		log.info(save.toString());
		// Assert.assertNotNull(save.getId());
	}

}
