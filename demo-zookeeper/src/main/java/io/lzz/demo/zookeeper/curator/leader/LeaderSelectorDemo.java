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

package io.lzz.demo.zookeeper.curator.leader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
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
 * @author q1219331697
 *
 */
public class LeaderSelectorDemo {

	private static final String PATH = "/examples/leader";
	private static final int CLIENT_QTY = 10;

	/**
	 * LeaderSelector使用的时候主要涉及下面几个类：
	 * 
	 * LeaderSelector
	 * LeaderSelectorListener
	 * LeaderSelectorListenerAdapter
	 * CancelLeadershipException
	 * 
	 * 异常处理 LeaderSelectorListener类继承ConnectionStateListener。
	 * LeaderSelector必须小心连接状态的改变。如果实例成为leader,
	 * 它应该响应SUSPENDED 或 LOST。
	 * 当 SUSPENDED 状态出现时， 实例必须假定在重新连接成功之前它可能不再是leader了。
	 * 如果LOST状态出现， 实例不再是leader， takeLeadership方法返回。
	 * 
	 * 重要: 推荐处理方式是当收到SUSPENDED 或 LOST时抛出CancelLeadershipException异常.。
	 * 这会导致LeaderSelector实例中断并取消执行takeLeadership方法的异常.。
	 * 这非常重要， 你必须考虑扩展LeaderSelectorListenerAdapter.
	 * LeaderSelectorListenerAdapter提供了推荐的处理逻辑。
	 */

	public static void main(String[] args) throws Exception {
		List<CuratorFramework> clients = Lists.newArrayList();
		List<LeaderSelectorAdapter> examples = Lists.newArrayList();
		// TestingServer server = new TestingServer();
		try {
			for (int i = 0; i < CLIENT_QTY; i++) {
				CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
						new ExponentialBackoffRetry(20000, 3));
				clients.add(client);
				LeaderSelectorAdapter selectorAdapter = new LeaderSelectorAdapter(client, PATH, "Client #" + i);
				examples.add(selectorAdapter);
				client.start();

				// 类似LeaderLatch,LeaderSelector必须start: leaderSelector.start();
				// 一旦启动，当实例取得领导权时你的listener的takeLeadership()方法被调用。
				// 而takeLeadership()方法只有领导权被释放时才返回。
				// 当你不再使用LeaderSelector实例时，应该调用它的close方法。
				selectorAdapter.start();
			}
			System.out.println("Press enter/return to quit\n");

			// 线程挂起，等待输入
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} finally {
			System.out.println("Shutting down...");
			for (LeaderSelectorAdapter exampleClient : examples) {
				CloseableUtils.closeQuietly(exampleClient);
			}
			for (CuratorFramework client : clients) {
				CloseableUtils.closeQuietly(client);
			}
			// CloseableUtils.closeQuietly(server);
		}
	}
}
