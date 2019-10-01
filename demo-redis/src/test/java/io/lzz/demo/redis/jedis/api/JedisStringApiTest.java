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

package io.lzz.demo.redis.jedis.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author q1219331697
 *
 */
public class JedisStringApiTest {

	private static final Logger log = LoggerFactory.getLogger(JedisStringApiTest.class);

	@Test
	public void testSet() {
		JedisStringApi jedisStringApi = new JedisStringApi();
		while (true) {
			// jedisStringApi.set(JedisFactory.getJedis());
			// jedisStringApi.set(JedisFactory.getJedisByJedisPool());
			jedisStringApi.set(JedisFactory.getJedisBySentinel());

			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		}
	}

}
