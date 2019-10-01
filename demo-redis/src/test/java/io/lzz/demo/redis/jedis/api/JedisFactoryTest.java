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
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author q1219331697
 *
 */
public class JedisFactoryTest {

	private static final Logger log = LoggerFactory.getLogger(JedisFactoryTest.class);

	@Test
	public void testGetJedis() {
		Jedis jedis = JedisFactory.getJedis();
		String ping = jedis.ping();
		Assert.isTrue("PONG".equalsIgnoreCase(ping), ping);
		log.info("ping common : {}", ping);
	}

	@Test
	public void testGetJedisByJedisPool() {
		Jedis jedis = JedisFactory.getJedisByJedisPool();
		String ping = jedis.ping();
		jedis.close();

		Assert.isTrue("PONG".equalsIgnoreCase(ping), ping);
		log.info("ping common : {}", ping);
	}

	@Test
	public void testGetJedisBySentinel() {
		Jedis jedis = JedisFactory.getJedisBySentinel();
		String ping = jedis.ping();
		jedis.close();

		Assert.isTrue("PONG".equalsIgnoreCase(ping), ping);
		log.info("ping common : {}", ping);
	}

	@Test
	public void testGetJedisByJedisCluster() {
		JedisCluster jedisCluster = JedisFactory.getJedisByJedisCluster();
		String returnValue = jedisCluster.setex("test", 10, "test-value");
		Assert.isTrue("ok".equalsIgnoreCase(returnValue), returnValue);
		log.info("jedis cluster [set] common : {}", returnValue);
	}

}
