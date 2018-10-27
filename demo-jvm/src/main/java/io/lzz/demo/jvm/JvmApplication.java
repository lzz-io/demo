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

package io.lzz.demo.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.lzz.demo.jvm.entity.User;

/**
 * @author q1219331697
 *
 */
@RestController
@SpringBootApplication
public class JvmApplication implements Runnable {

	private List<User> users = new ArrayList<User>();

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JvmApplication.class, args);
	}

	@GetMapping(value = "/")
	public String index() {
		run();
		return "hello world";
	}

	@Override
	public void run() {
		int i = 0;
		while (true) {
			users.add(new User(i, UUID.randomUUID().toString()));

			if (i % 10000 == 0) {
				users.clear();
			}

			i++;
			// if (i == Integer.MIN_VALUE) { // true at Integer.MAX_VALUE +1
			// break;
			// }

		}

	}

}
