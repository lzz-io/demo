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

package io.lzz.demo.zookeeper.api.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * Node Cache与Path Cache类似，Node Cache只是监听某一个特定的节点。它涉及到下面的三个类：
 * 
 * NodeCache - Node Cache实现类
 * NodeCacheListener - 节点监听器
 * ChildData - 节点数据
 * 注意：使用cache，依然要调用它的start()方法，使用完后调用close()方法。
 * 
 * getCurrentData()将得到节点当前的状态，通过它的状态可以得到当前的值。
 * 
 * 注意：示例中的Thread.sleep(10)可以注释，但是注释后事件监听的触发次数会不全，
 * 这可能与NodeCache的实现原理有关，不能太过频繁的触发事件！
 * 
 * 注意：NodeCache只能监听一个节点的状态变化。
 * 
 * @author q1219331697
 *
 */
public class NodeCacheDemo {

	private static final String PATH = "/example/cache";

	public static void main(String[] args) throws Exception {
		// TestingServer server = new TestingServer();
		CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
				new ExponentialBackoffRetry(1000, 3));
		client.start();
		client.create().creatingParentsIfNeeded().forPath(PATH);

		// Node Cache与Path Cache类似，Node Cache只是监听某一个特定的节点。
		// 它涉及到下面的三个类：
		//
		// NodeCache - Node Cache实现类
		// NodeCacheListener - 节点监听器
		// ChildData - 节点数据
		// 注意：使用cache，依然要调用它的start()方法，使用完后调用close()方法。
		// getCurrentData()将得到节点当前的状态，通过它的状态可以得到当前的值。
		final NodeCache cache = new NodeCache(client, PATH);
		NodeCacheListener listener = () -> {
			ChildData data = cache.getCurrentData();
			if (null != data) {
				System.out.println("节点数据：" + new String(cache.getCurrentData().getData()));
			} else {
				System.out.println("节点被删除!");
			}
		};
		cache.getListenable().addListener(listener);
		cache.start();

		// 注意：示例中的Thread.sleep(10)可以注释，但是注释后事件监听的触发次数会不全，
		// 这可能与NodeCache的实现原理有关，不能太过频繁的触发事件！
		client.setData().forPath(PATH, "01".getBytes());
		Thread.sleep(100);
		client.setData().forPath(PATH, "02".getBytes());
		Thread.sleep(100);

		client.delete().deletingChildrenIfNeeded().forPath(PATH);
		Thread.sleep(1000 * 2L);

		cache.close();
		client.close();

		System.out.println("OK!");
	}
}
