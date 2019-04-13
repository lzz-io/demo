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

package io.lzz.demo.redis.controller;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.lzz.demo.core.BaseController;

/**
 * @author q1219331697
 *
 */
@RestController
@RequestMapping("/redis")
public class RedisController implements BaseController {

	private static final Logger log = LoggerFactory.getLogger(RedisController.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	@GetMapping(value = "")
	public String index() {
		// ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		// Duration timeout = Duration.ofSeconds(10L);
		// opsForValue.set("key", "value", timeout);
		// String value = opsForValue.get("key");
		// log.info(value);

		// HashOperations<String, Object, Object> opsForHash =
		// redisTemplate.opsForHash();
		// // opsForHash.put("key", "hashKey", "value");
		// opsForHash.put("key", new Random().nextInt(), new Random().nextInt());
		// Object object = opsForHash.entries("key");
		// log.info(object.toString());

		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		String key = new Random().toString();
		opsForValue.set(key, key, 10, TimeUnit.SECONDS);
		
		// redisTemplate.
		return "" + System.currentTimeMillis();
	}

}
