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

package io.lzz.demo.zookeeper.curator.counter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.google.common.collect.Lists;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 分布式计数器
 * 顾名思义，计数器是用来计数的, 利用ZooKeeper可以实现一个集群共享的计数器。
 * 只要使用相同的path就可以得到最新的计数器值，
 * 这是由ZooKeeper的一致性保证的。Curator有两个计数器，
 * 一个是用int来计数(SharedCount)，一个用long来计数(DistributedAtomicLong)。
 * 
 * 
 * 分布式int计数器—SharedCount
 * 这个类使用int类型来计数。 主要涉及三个类。
 * 
 * SharedCount
 * SharedCountReader
 * SharedCountListener
 * SharedCount代表计数器，
 * 可以为它增加一个SharedCountListener，
 * 当计数器改变时此Listener可以监听到改变的事件，
 * 而SharedCountReader可以读取到最新的值，
 * 包括字面值和带版本信息的值VersionedValue。
 * 
 * 在这个例子中，我们使用baseCount来监听计数值
 * ( addListener方法来添加SharedCountListener )。
 * 任意的SharedCount， 只要使用相同的path，都可以得到这个计数值。
 * 然后我们使用5个线程为计数值增加一个10以内的随机数。
 * 相同的path的SharedCount对计数值进行更改，
 * 将会回调给baseCount的SharedCountListener。
 * 
 * 
 * 这里我们使用trySetCount去设置计数器。
 * 第一个参数提供当前的VersionedValue,如果期间其它client更新了此计数值，
 * 你的更新可能不成功， 但是这时你的client更新了最新的值，
 * 所以失败了你可以尝试再更新一次。 而setCount是强制更新计数器的值。
 * 
 * 注意计数器必须start,使用完之后必须调用close关闭它。
 * 
 * 强烈推荐使用ConnectionStateListener。
 * 在本例中SharedCountListener扩展ConnectionStateListener。
 * 
 * @author q1219331697
 *
 */
public class SharedCounterDemo implements SharedCountListener {

	private static final int QTY = 5;
	private static final String PATH = "/examples/counter";

	public static void main(String[] args) throws Exception {
		CuratorFramework client = null;
		final Random rand = new Random();
		SharedCounterDemo example = new SharedCounterDemo();
		try {
			client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST, new ExponentialBackoffRetry(1000, 3));
			client.start();

			SharedCount baseCount = new SharedCount(client, PATH, 0);
			baseCount.addListener(example);
			baseCount.start();

			List<SharedCount> examples = Lists.newArrayList();
			ExecutorService service = Executors.newFixedThreadPool(QTY);
			for (int i = 0; i < QTY; i++) {
				final SharedCount count = new SharedCount(client, PATH, 0);
				examples.add(count);
				Callable<Void> task = () -> {
					count.start();
					Thread.sleep(rand.nextInt(10000));
					// 这里我们使用trySetCount去设置计数器。
					// 第一个参数提供当前的VersionedValue,如果期间其它client更新了此计数值，
					// 你的更新可能不成功， 但是这时你的client更新了最新的值，所以失败了你可以尝试再更新一次。
					// 而setCount是强制更新计数器的值。
					System.out
							.println("Increment:" + count.trySetCount(count.getVersionedValue(), count.getCount() + 1));
					return null;
				};
				service.submit(task);
			}

			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);

			for (int i = 0; i < QTY; ++i) {
				examples.get(i).close();
			}
			baseCount.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.delete().deletingChildrenIfNeeded().forPath("/examples");
		}

		// Thread.sleep(Integer.MAX_VALUE);
	}

	@Override
	public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
		System.out.println("State changed: " + arg1.toString());
	}

	@Override
	public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
		System.out.println("Counter's value is changed to " + newCount);
	}
}
