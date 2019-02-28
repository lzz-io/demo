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

package io.lzz.demo.spring.test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.lzz.demo.spring.test.entity.User;
import io.lzz.demo.spring.test.service.UserService;

/**
 * @author q1219331697
 *
 */
@RestController
@RequestMapping("/")
public class IndexController {

	@Autowired
	private UserService userService;

	@GetMapping(value = "/user")
	public List<User> index() {
		return userService.findAll();
	}

	@GetMapping(value = "/user/{id}")
	public User show(@PathVariable Integer id) {
		return userService.findById(id);
	}

	@GetMapping(value = "/user/new")
	public String editNew() {
		return "add page";
	}

	@PostMapping(value = "/user")
	public User create(@RequestBody User user) {
		return user;
	}

	@GetMapping(value = "/user/{id}/edit")
	public String edit(@PathVariable Integer id) {
		return "edit page";
	}

	@PutMapping(value = "/user/{id}")
	public User update(@PathVariable Integer id, @RequestBody User user) {
		return userService.save(user);
	}

	@DeleteMapping(value = "/user/{id}")
	public String destroy(@PathVariable Integer id) {
		userService.delete(id);
		return "Success";
	}

}
