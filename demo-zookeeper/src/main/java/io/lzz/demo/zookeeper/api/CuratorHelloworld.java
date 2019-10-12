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

package io.lzz.demo.zookeeper.api;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author q1219331697
 *
 */
public class CuratorHelloworld {

	public static void main(String[] args) throws Exception {
		String connectString = "localhost:2181";
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
		client.start();

		String demoPath = "/curator-demo";
		if (client.checkExists().forPath(demoPath) == null) {
			// client.createContainers(path);
			// CreateMode.EPHEMERAL 临时节点
			client.create().withMode(CreateMode.EPHEMERAL).forPath(demoPath);
			client.setData().forPath(demoPath, "curator-demo-data".getBytes());
		}

		byte[] bs = client.getData().forPath(demoPath);
		System.out.println(new String(bs));

		client.delete().deletingChildrenIfNeeded().forPath(demoPath);
		
		client.close();
	}

}
