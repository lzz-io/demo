/*
 * Copyright (C) qq:1219331697
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

package io.lzz.demo.kafka.startserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import kafka.server.KafkaServerStartable;

/**
 * @author q1219331697
 *
 */
public class ZkServerStart {

	public static void main(String[] args) throws Exception {
		ClassLoader classLoader = ZkServerStart.class.getClassLoader();
		System.out.println(classLoader);
		InputStream zkInputStream = classLoader.getResourceAsStream("config/zookeeper.properties");
		InputStream kafkaInputStream = classLoader.getResourceAsStream("config/server.properties");

		Properties properties = new Properties();
		properties.load(zkInputStream);
		System.out.println(properties);
		zkInputStream.close();

		// properties.load(kafkaInputStream);
		// System.out.println(properties);

		QuorumPeerConfig config = new QuorumPeerConfig();
		config.parseProperties(properties);

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.readFrom(config);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ZooKeeperServerMain zooKeeperServerMain = new ZooKeeperServerMain();
					zooKeeperServerMain.runFromConfig(serverConfig);

					// QuorumPeerMain quorumPeerMain = new QuorumPeerMain();
					// quorumPeerMain.runFromConfig(config);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "zk-server").start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = classLoader.getResourceAsStream("config/server.properties");
					Properties p = new Properties();
					p.load(is);
					is.close();
					KafkaServerStartable kafkaServerStartable = KafkaServerStartable.fromProps(p);
					kafkaServerStartable.startup();
					kafkaServerStartable.awaitShutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "kafka-server").start();

	}
}
