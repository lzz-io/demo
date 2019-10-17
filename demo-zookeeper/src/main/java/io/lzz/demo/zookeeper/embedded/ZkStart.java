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

package io.lzz.demo.zookeeper.embedded;

import java.io.File;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

/**
 * @author q1219331697
 *
 */
public class ZkStart {

	public static void main(String[] args) throws Exception {

		Properties properties = new Properties();
		properties.setProperty("tickTime", "2000");
		// properties.setProperty("dataDir",
		// new File(System.getProperty("java.io.tmpdir"),
		// "zookeeper").getAbsolutePath());
		properties.setProperty("dataDir", new File("./target").getAbsolutePath());
		properties.setProperty("clientPort", "2181");
		properties.setProperty("initLimit", "10");
		properties.setProperty("syncLimit", "5");

		QuorumPeerConfig config = new QuorumPeerConfig();
		config.parseProperties(properties);

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.readFrom(config);

		ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
		zkServer.runFromConfig(serverConfig);
	}

}
