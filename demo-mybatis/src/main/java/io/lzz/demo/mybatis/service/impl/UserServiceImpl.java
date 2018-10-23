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

package io.lzz.demo.mybatis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.lzz.demo.mybatis.entity.User1;
import io.lzz.demo.mybatis.mapper.UserMapper1;
import io.lzz.demo.mybatis.service.UserService;

/**
 * @author q1219331697
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper1 userMapper;

	@Override
	public List<User1> findAll() {
		return userMapper.findAll();
	}

	@Override
	public User1 findById(Long id) {
		return userMapper.findById(id);
	}

	@Override
	public User1 insert(User1 user) {
		userMapper.insert(user);
		return user;
	}

	@Override
	public User1 update(User1 user) {
		userMapper.update(user);
		return user;
	}

	@Override
	public Integer delete(Long id) {
		userMapper.delete(id);
		return 1;
	}

}
