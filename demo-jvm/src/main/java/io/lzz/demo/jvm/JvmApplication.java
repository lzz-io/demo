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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.lzz.demo.jvm.entity.User;

/**
 * @author q1219331697
 *
 */
@RestController
@SpringBootApplication
public class JvmApplication {

	private static final Logger log = LoggerFactory.getLogger(JvmApplication.class);

	private static HashMap<String, User> map = new HashMap<>();

	int j = 0;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JvmApplication.class, args);
	}

	@GetMapping(value = "/")
	public String index() {
		for (int i = 0; i < 100; i++) {
			new Thread(() -> {
				HashMap<String, User> map2 = new HashMap<>();
				map2.put(String.valueOf(j), new User(Thread.currentThread().getId(), Thread.currentThread().getName()));
				j++;

				map = map2;
				log.info("{}", map);
			}, String.valueOf(i)).start();
		}

		return "hello world";
	}

}
