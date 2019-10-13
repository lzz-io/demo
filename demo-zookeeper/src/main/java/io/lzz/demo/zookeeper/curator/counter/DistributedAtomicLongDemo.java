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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

import com.google.common.collect.Lists;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 分布式long计数器—DistributedAtomicLong
 * 再看一个Long类型的计数器。 除了计数的范围比SharedCount大了之外，
 * 它首先尝试使用乐观锁的方式设置计数器，
 * 如果不成功(比如期间计数器已经被其它client更新了)，
 * 它使用InterProcessMutex方式来更新计数值。
 * 
 * 
 * 此计数器有一系列的操作：
 * 
 * get(): 获取当前值
 * increment()： 加一
 * decrement(): 减一
 * add()： 增加特定的值
 * subtract(): 减去特定的值
 * trySet(): 尝试设置计数值
 * forceSet(): 强制设置计数值
 * 你必须检查返回结果的succeeded()， 它代表此操作是否成功。
 * 如果操作成功， preValue()代表操作前的值，
 * postValue()代表操作后的值。
 * 
 * 
 * 
 * @author q1219331697
 *
 */
public class DistributedAtomicLongDemo {

	private static final int QTY = 5;
	private static final String PATH = "/examples/counter";

	public static void main(String[] args) throws Exception {
		List<DistributedAtomicLong> examples = Lists.newArrayList();
		CuratorFramework client = null;
		try {
			client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST, new ExponentialBackoffRetry(1000, 3));
			client.start();
			ExecutorService service = Executors.newFixedThreadPool(QTY);
			for (int i = 0; i < QTY; ++i) {
				final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));

				examples.add(count);
				Callable<Void> task = () -> {
					try {
						AtomicValue<Long> value = count.increment();
						System.out.println("succeed: " + value.succeeded());
						if (value.succeeded())
							System.out.println("Increment: from " + value.preValue() + " to " + value.postValue());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				};
				service.submit(task);
			}

			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
			// Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.delete().deletingChildrenIfNeeded().forPath("/examples");
		}

	}
}
