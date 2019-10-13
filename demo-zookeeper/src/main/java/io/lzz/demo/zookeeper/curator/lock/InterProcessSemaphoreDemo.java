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

package io.lzz.demo.zookeeper.curator.lock;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 信号量—Shared Semaphore
 * 一个计数的信号量类似JDK的Semaphore。
 * JDK中Semaphore维护的一组许可(permits)，而Curator中称之为租约(Lease)。
 * 有两种方式可以决定semaphore的最大租约数。
 * 第一种方式是用户给定path并且指定最大LeaseSize。
 * 第二种方式用户给定path并且使用SharedCountReader类。
 * 如果不使用SharedCountReader,
 * 必须保证所有实例在多进程中使用相同的(最大)租约数量,
 * 否则有可能出现A进程中的实例持有最大租约数量为10，
 * 但是在B进程中持有的最大租约数量为20，此时租约的意义就失效了。
 * 
 * 这次调用acquire()会返回一个租约对象。
 * 客户端必须在finally中close这些租约对象，否则这些租约会丢失掉。 但是，
 * 但是，如果客户端session由于某种原因比如crash丢掉，
 * 那么这些客户端持有的租约会自动close， 这样其它客户端可以继续使用这些租约。
 * 
 * 注意你可以一次性请求多个租约，如果Semaphore当前的租约不够，
 * 则请求线程会被阻塞。 同时还提供了超时的重载方法。
 * 
 * 
 * Shared Semaphore使用的主要类包括下面几个：
 * 
 * InterProcessSemaphoreV2
 * Lease
 * SharedCountReader
 * 
 * 
 * 首先我们先获得了5个租约， 最后我们把它还给了semaphore。
 * 接着请求了一个租约，因为semaphore还有5个租约，
 * 所以请求可以满足，返回一个租约，还剩4个租约。
 * 然后再请求一个租约，因为租约不够，阻塞到超时，
 * 还是没能满足，返回结果为null(租约不足会阻塞到超时，
 * 然后返回null，不会主动抛出异常；如果不设置超时时间，会一致阻塞)。
 * 
 * 
 * 上面说讲的锁都是公平锁(fair)。 总ZooKeeper的角度看，
 * 每个客户端都按照请求的顺序获得锁，不存在非公平的抢占的情况。
 * 
 * 
 * 
 * @author q1219331697
 *
 */
public class InterProcessSemaphoreDemo {

	private static final int MAX_LEASE = 10;
	private static final String PATH = "/examples/locks";

	public static void main(String[] args) throws Exception {
		FakeLimitedResource resource = new FakeLimitedResource();
		// try (TestingServer server = new TestingServer()) {
		try {
			CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
					new ExponentialBackoffRetry(1000, 3));
			client.start();

			InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, PATH, MAX_LEASE);
			Collection<Lease> leases = semaphore.acquire(5);
			System.out.println("get " + leases.size() + " leases");
			Lease lease = semaphore.acquire();
			System.out.println("get another lease");

			resource.use();

			// 阻塞到超时，还是没能满足，返回结果为null(租约不足会阻塞到超时，
			// 然后返回null，
			// 不会主动抛出异常；如果不设置超时时间，会一致阻塞)
			Collection<Lease> leases2 = semaphore.acquire(5, 10, TimeUnit.SECONDS);
			System.out.println("Should timeout and acquire return " + leases2);

			System.out.println("return one lease");
			semaphore.returnLease(lease);
			System.out.println("return another 5 leases");
			semaphore.returnAll(leases);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

}
