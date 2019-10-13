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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 这个锁和上面的InterProcessMutex相比，就是少了Reentrant的功能，
 * 也就意味着它不能在同一个线程中重入。
 * 这个类是InterProcessSemaphoreMutex,使用方法和InterProcessMutex类似。
 * 
 * 运行后发现，有且只有一个client成功获取第一个锁(第一个acquire()方法返回true)，
 * 然后它自己阻塞在第二个acquire()方法，获取第二个锁超时；
 * 其他所有的客户端都阻塞在第一个acquire()方法超时并且抛出异常。
 * 
 * 这样也就验证了InterProcessSemaphoreMutex实现的锁是不可重入的。
 * 
 * 
 * @author q1219331697
 *
 */
public class InterProcessSemaphoreMutexDemo {

	private InterProcessSemaphoreMutex lock;
	private final FakeLimitedResource resource;
	private final String clientName;

	public InterProcessSemaphoreMutexDemo(CuratorFramework client, String lockPath, FakeLimitedResource resource,
			String clientName) {
		this.resource = resource;
		this.clientName = clientName;
		this.lock = new InterProcessSemaphoreMutex(client, lockPath);
	}

	public void doWork(long time, TimeUnit unit) throws Exception {
		if (!lock.acquire(time, unit)) {
			throw new IllegalStateException(clientName + " 不能得到互斥锁");
		}
		System.out.println(clientName + " 已获取到互斥锁");
		// if (!lock.acquire(time, unit)) {
		// throw new IllegalStateException(clientName + " 不能得到互斥锁");
		// }
		// System.out.println(clientName + " 再次获取到互斥锁");
		try {
			System.out.println(clientName + " get the lock");
			resource.use(); // access resource exclusively
		} finally {
			System.out.println(clientName + " releasing the lock");
			lock.release(); // always release the lock in a finally block
			// lock.release(); // 获取锁几次 释放锁也要几次
		}
	}

	private static final int QTY = 5;
	private static final int REPETITIONS = QTY * 10;
	private static final String PATH = "/examples/locks";

	public static void main(String[] args) throws Exception {
		final FakeLimitedResource resource = new FakeLimitedResource();
		ExecutorService service = Executors.newFixedThreadPool(QTY);
		// final TestingServer server = new TestingServer();
		try {
			for (int i = 0; i < QTY; ++i) {
				final int index = i;
				Callable<Void> task = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
								new ExponentialBackoffRetry(1000, 3));
						try {
							client.start();
							final InterProcessSemaphoreMutexDemo example = new InterProcessSemaphoreMutexDemo(client,
									PATH, resource, "Client " + index);
							for (int j = 0; j < REPETITIONS; ++j) {
								example.doWork(10, TimeUnit.SECONDS);
							}
						} catch (Throwable e) {
							e.printStackTrace();
						} finally {
							CloseableUtils.closeQuietly(client);
						}
						return null;
					}
				};
				service.submit(task);
			}
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
		} finally {
			// CloseableUtils.closeQuietly(server);
		}
		// Thread.sleep(Integer.MAX_VALUE);
	}

}
