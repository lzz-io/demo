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

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

/**
 * 
 * 异常处理
 * LeaderSelectorListener类继承ConnectionStateListener。
 * LeaderSelector必须小心连接状态的改变。如果实例成为leader,
 * 它应该响应SUSPENDED 或 LOST。 当 SUSPENDED 状态出现时，
 * 实例必须假定在重新连接成功之前它可能不再是leader了。
 * 如果LOST状态出现， 实例不再是leader， takeLeadership方法返回。
 * 
 * 重要: 推荐处理方式是当收到SUSPENDED 或
 * LOST时抛出CancelLeadershipException异常.。
 * 这会导致LeaderSelector实例中断并取消执行takeLeadership方法的异常.。
 * 这非常重要，
 * 你必须考虑扩展LeaderSelectorListenerAdapter.
 * LeaderSelectorListenerAdapter提供了推荐的处理逻辑。
 * 
 * @author q1219331697
 *
 */
public class LeaderSelectorAdapter extends LeaderSelectorListenerAdapter implements Closeable {

	private final String name;
	private final LeaderSelector leaderSelector;
	private final AtomicInteger leaderCount = new AtomicInteger();

	/**
	 * 你可以在takeLeadership进行任务的分配等等，并且不要返回，
	 * 如果你想要要此实例一直是leader的话可以加一个死循环。调用
	 * leaderSelector.autoRequeue();保证在此实例释放领导权之后还可能获得领导权。
	 * 在这里我们使用AtomicInteger来记录此client获得领导权的次数，
	 * 它是”fair”， 每个client有平等的机会获得领导权。
	 * 
	 * 对比可知，LeaderLatch必须调用close()方法才会释放领导权，
	 * 而对于LeaderSelector，通过LeaderSelectorListener可以对领导权进行控制，
	 * 在适当的时候释放领导权，这样每个节点都有可能获得领导权。
	 * 从而，LeaderSelector具有更好的灵活性和可控性，
	 * 
	 * 建议有LeaderElection应用场景下优先使用LeaderSelector。
	 */

	/**
	 * @param client
	 * @param path
	 * @param name
	 */
	public LeaderSelectorAdapter(CuratorFramework client, String path, String name) {
		this.name = name;

		// create a leader selector using the given path for management
		// all participants in a given leader selection must use the same path
		// ExampleClient here is also a LeaderSelectorListener but this isn't required
		leaderSelector = new LeaderSelector(client, path, this);

		// for most cases you will want your instance to requeue when it relinquishes
		// leadership
		leaderSelector.autoRequeue();
	}

	public void start() throws IOException {
		// the selection for this instance doesn't start until the leader selector is
		// started
		//
		// leader selection is done in the background so this call to
		// leaderSelector.start() returns immediately
		leaderSelector.start();
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		// we are now the leader. This method should not return until we want to
		// relinquish leadership

		final int waitSeconds = (int) (5 * Math.random()) + 1;

		System.out.println(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
		System.out.println(name + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
		} catch (InterruptedException e) {
			System.err.println(name + " was interrupted.");
			Thread.currentThread().interrupt();
		} finally {
			System.out.println(name + " relinquishing leadership.\n");
		}
	}

	@Override
	public void close() throws IOException {
		leaderSelector.close();
	}

}
