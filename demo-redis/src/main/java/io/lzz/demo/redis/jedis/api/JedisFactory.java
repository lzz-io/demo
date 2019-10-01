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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * jedis 工厂
 * 
 * @author q1219331697
 *
 */
public class JedisFactory {

	private static final Logger log = LoggerFactory.getLogger(JedisFactory.class);

	// 双重检查锁，要volatile关键字
	private volatile static JedisPool jedisPool = null;

	// 静态内部类实现单例
	private static JedisSentinelPool jedisSentinelPool = null;

	// 非线程安全
	// private static JedisCluster jedisCluster;
	// private static ThreadLocal<JedisCluster> threadLocal = new ThreadLocal<>();

	private JedisFactory() {
	}

	public static Jedis getJedis() {
		String host = "127.0.0.1";
		int port = 6379;
		Jedis jedis = new Jedis(host, port);
		// jedis.auth("123");
		return jedis;
	}

	public static Jedis getJedisByJedisPool() {
		if (jedisPool != null) {
			return jedisPool.getResource();
		}

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(20);
		config.setMaxIdle(15);
		config.setMinIdle(10);
		config.setMaxWaitMillis(30000L);
		config.setTestOnBorrow(true);

		// 双重检查锁
		synchronized (JedisFactory.class) {
			if (jedisPool == null) {
				jedisPool = new JedisPool(config, "127.0.0.1", 6379);
			}
		}
		return jedisPool.getResource();
	}

	public static Jedis getJedisBySentinel() {
		if (jedisSentinelPool != null) {
			log.info("1 master host: {}", jedisSentinelPool.getCurrentHostMaster());
			return jedisSentinelPool.getResource();
		}

		// Set<String> sentinels = new HashSet<>();
		// sentinels.add("127.0.0.1:26379");
		// sentinels.add("127.0.0.1:26380");
		// sentinels.add("127.0.0.1:26381");
		//
		// GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
		// poolConfig.setMaxTotal(20);
		// poolConfig.setMinIdle(10);
		// poolConfig.setMaxWaitMillis(30000L);
		// poolConfig.setMaxIdle(15);
		// poolConfig.setTestOnBorrow(true);

		// JedisSentinelPoolInit.jedisSentinelPool.getResource();

		// jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, poolConfig);
		// log.info("2 master host: {}", jedisSentinelPool.getCurrentHostMaster());
		// return jedisSentinelPool.getResource();
		jedisSentinelPool = JedisSentinelPoolInit.jedisSentinelPool;
		return jedisSentinelPool.getResource();
	}

	// 静态内部类实现单例
	private static class JedisSentinelPoolInit {
		private static JedisSentinelPool jedisSentinelPool = null;
		static {
			Set<String> sentinels = new HashSet<>();
			sentinels.add("127.0.0.1:26379");
			sentinels.add("127.0.0.1:26380");
			sentinels.add("127.0.0.1:26381");

			JedisPoolConfig poolConfig = new JedisPoolConfig();
			poolConfig.setMaxTotal(20);
			poolConfig.setMinIdle(10);
			poolConfig.setMaxWaitMillis(30000L);
			poolConfig.setMaxIdle(15);
			poolConfig.setTestOnBorrow(true);

			jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, poolConfig);
			// 设置redis-server密码
			// jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, poolConfig,
			// "123");
			log.info("2 master host: {}", jedisSentinelPool.getCurrentHostMaster());
		}
	}

	public static JedisCluster getJedisByJedisCluster() {
		// JedisCluster jedisCluster = threadLocal.get();
		// if (jedisCluster != null) {
		// return jedisCluster;
		// }

		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("127.0.0.1", 7000));
		nodes.add(new HostAndPort("127.0.0.1", 7001));
		nodes.add(new HostAndPort("127.0.0.1", 7002));
		nodes.add(new HostAndPort("127.0.0.1", 7003));
		nodes.add(new HostAndPort("127.0.0.1", 7004));
		nodes.add(new HostAndPort("127.0.0.1", 7005));

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(20);
		poolConfig.setMinIdle(10);
		poolConfig.setMaxWaitMillis(30000L);
		poolConfig.setMaxIdle(15);
		poolConfig.setTestOnBorrow(true);

		JedisCluster jedisCluster = new JedisCluster(nodes, poolConfig);
		System.out.println(jedisCluster);
		return jedisCluster;
	}

	public static void close(Jedis jedis) {
		jedis.close();
	}

	public static void close(JedisCluster jedisCluster) {
		try {
			jedisCluster.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
}
