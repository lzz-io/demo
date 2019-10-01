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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import redis.clients.jedis.Jedis;

/**
 * 简单Jedis
 * 
 * @author q1219331697
 *
 */
public class JedisStringApi {

	public void set(Jedis jedis) {

		try {
			String key = UUID.randomUUID().toString().replace("-", "");
			jedis.set("k:" + key, "v-" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
			String value = jedis.get("k:" + key);
			System.out.println(value);
		} finally {
			if (jedis != null && jedis.isConnected()) {
				jedis.close();
			}
		}
	}

	public static void main(String[] args) {
		JedisStringApi jedisStringApi = new JedisStringApi();
		int count = 0;
		while (true) {
			try {
				Jedis jedis = JedisFactory.getJedisBySentinel();
				jedisStringApi.set(jedis);
				count++;
				if (count % 1000 == 0) {
					System.out.println(count);
				}

				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (count == Integer.MAX_VALUE) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
