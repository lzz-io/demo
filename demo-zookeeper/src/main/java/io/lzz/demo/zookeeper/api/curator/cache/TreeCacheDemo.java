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
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * Tree Cache可以监控整个树上的所有节点，类似于PathCache和NodeCache的组合，主要涉及到下面四个类：
 * 
 * TreeCache - Tree Cache实现类
 * TreeCacheListener - 监听器类
 * TreeCacheEvent - 触发的事件类
 * ChildData - 节点数据
 * 
 * 注意：在此示例中没有使用Thread.sleep(10)，但是事件触发次数也是正常的。
 * 
 * 注意：TreeCache在初始化(调用start()方法)的时候会回调TreeCacheListener实例一个事件TreeCacheEvent，
 * 而回调的TreeCacheEvent对象的Type为INITIALIZED，ChildData为null，
 * 此时event.getData().getPath()很有可能导致空指针异常，这里应该主动处理并避免这种情况。
 * 
 * @author q1219331697
 *
 */
public class TreeCacheDemo {

	private static final String PATH = "/example/cache";

	public static void main(String[] args) throws Exception {
		// TestingServer server = new TestingServer();
		CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
				new ExponentialBackoffRetry(1000, 3));
		client.start();
		client.create().creatingParentsIfNeeded().forPath(PATH);

		// Tree Cache可以监控整个树上的所有节点，类似于PathCache和NodeCache的组合，
		// 主要涉及到下面四个类：
		//
		// TreeCache - Tree Cache实现类
		// TreeCacheListener - 监听器类
		// TreeCacheEvent - 触发的事件类
		// ChildData - 节点数据
		TreeCache cache = new TreeCache(client, PATH);
		TreeCacheListener listener = (client1, event) -> System.out.println(
				"事件类型：" + event.getType() + " | 路径：" + (null != event.getData() ? event.getData().getPath() : null));
		cache.getListenable().addListener(listener);

		// 注意：在此示例中没有使用Thread.sleep(10)，但是事件触发次数也是正常的。

		// 注意：TreeCache在初始化(调用start()方法)的时候会回调TreeCacheListener实例
		// 一个事TreeCacheEvent，
		// 而回调的TreeCacheEvent对象的Type为INITIALIZED，ChildData为null，
		// 此时event.getData().getPath()很有可能导致空指针异常，
		// 这里应该主动处理并避免这种情况。
		cache.start();

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
