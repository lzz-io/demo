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

package io.lzz.demo.zookeeper.api.curator.leader;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import com.google.common.collect.Lists;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * Leader选举
 * 在分布式计算中， leader elections是很重要的一个功能，
 * 这个选举过程是这样子的： 指派一个进程作为组织者，将任务分发给各节点。
 * 
 * 在任务开始前， 哪个节点都不知道谁是leader(领导者)或者coordinator(协调者).
 * 当选举算法开始执行后，
 * 每个节点最终会得到一个唯一的节点作为任务leader.
 * 除此之外， 选举还经常会发生在leader意外宕机的情况下，新的leader要被选举出来。
 * 
 * 在zookeeper集群中，leader负责写操作，然后通过Zab协议实现follower的同步，
 * leader或者follower都可以处理读操作。
 * 
 * Curator 有两种leader选举的recipe,分别是LeaderSelector和LeaderLatch。
 * 
 * 前者是所有存活的客户端不间断的轮流做Leader，大同社会。
 * 后者是一旦选举出Leader，除非有客户端挂掉重新触发选举，否则不会交出领导权。某党?
 * 
 * 
 * 异常处理： LeaderLatch实例可以增加ConnectionStateListener来监听网络连接问题。
 * 当 SUSPENDED 或 LOST 时, leader不再认为自己还是leader。
 * 当LOST后连接重连后RECONNECTED,LeaderLatch会删除先前的ZNode然后重新创建一个。
 * LeaderLatch用户必须考虑导致leadership丢失的连接问题。
 * 强烈推荐你使用ConnectionStateListener。
 * 
 * 
 * @author q1219331697
 *
 */
public class LeaderLatchDemo {

	private static final String PATH = "/examples/leader";
	private static final int CLIENT_QTY = 10;

	public static void main(String[] args) throws Exception {
		List<CuratorFramework> clients = Lists.newArrayList();
		List<LeaderLatch> examples = Lists.newArrayList();
		// TestingServer server=new TestingServer();
		try {
			for (int i = 0; i < CLIENT_QTY; i++) {
				CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
						new ExponentialBackoffRetry(20000, 3));

				clients.add(client);
				LeaderLatch latch = new LeaderLatch(client, PATH, "Client #" + i);
				latch.addListener(new LeaderLatchListener() {

					@Override
					public void isLeader() {
						System.out.println(latch.getId() + " I am Leader");
					}

					@Override
					public void notLeader() {
						System.out.println(latch.getId() + " I am not Leader");
					}
				});
				examples.add(latch);
				client.start();

				// 一旦启动，LeaderLatch会和其它使用相同latch
				// path的其它LeaderLatch交涉，然后其中一个最终会被选举为leader，
				// 可以通过hasLeadership方法查看LeaderLatch实例是否leader：
				latch.start();

				// await是一个阻塞方法， 尝试获取leader地位，但是未必能上位。
				// latch.await();
			}

			// 等待选举
			Thread.sleep(10000);
			LeaderLatch currentLeader = null;
			for (LeaderLatch latch : examples) {
				// leaderLatch.hasLeadership( ); //返回true说明当前实例是leader
				// 类似JDK的CountDownLatch，
				// LeaderLatch在请求成为leadership会block(阻塞)，
				// 一旦不使用LeaderLatch了，必须调用close方法。
				// 如果它是leader,会释放leadership，
				// 其它的参与者将会选举一个leader。
				if (latch.hasLeadership()) {
					currentLeader = latch;
				}
			}

			System.out.println("current leader is " + currentLeader.getId());
			System.out.println("release the leader " + currentLeader.getId());

			// 释放当前的领导权
			currentLeader.close();

			// 等待选举
			Thread.sleep(5000);

			for (LeaderLatch latch : examples) {
				if (latch.hasLeadership()) {
					currentLeader = latch;
				}
			}
			System.out.println("current leader is " + currentLeader.getId());
			System.out.println("release the leader " + currentLeader.getId());
		} finally {
			for (LeaderLatch latch : examples) {
				if (null != latch.getState())
					CloseableUtils.closeQuietly(latch);
			}
			for (CuratorFramework client : clients) {
				CloseableUtils.closeQuietly(client);
			}
		}
	}
}
