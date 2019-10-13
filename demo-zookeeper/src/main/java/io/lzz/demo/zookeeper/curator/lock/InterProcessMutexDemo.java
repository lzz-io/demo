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
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 创建一个InterProcessMutexDemo类，
 * 它负责请求锁， 使用资源，释放锁这样一个完整的访问过程。
 * 
 * 分布式锁
 * 提醒：
 * 
 * 1.推荐使用ConnectionStateListener监控连接的状态，因为当连接LOST时你不再拥有锁
 * 
 * 2.分布式的锁全局同步， 这意味着任何一个时间点不会有两个客户端都拥有相同的锁。
 * 
 * 可重入共享锁—Shared Reentrant Lock
 * Shared意味着锁是全局可见的， 客户端都可以请求锁。 Reentrant和JDK的ReentrantLock类似，即可重入，
 * 意味着同一个客户端在拥有锁的同时，可以多次获取，不会被阻塞。 它是由类InterProcessMutex来实现。
 * 
 * 通过acquire()获得锁，并提供超时机制：
 * 
 * 通过release()方法释放锁。 InterProcessMutex 实例可以重用。
 * 
 * 如果你请求撤销当前的锁， 调用attemptRevoke()方法,
 * 注意锁释放时RevocationListener将会回调。
 * 
 * 
 * 二次提醒：错误处理 还是强烈推荐你使用ConnectionStateListener处理连接状态的改变。 当连接LOST时你不再拥有锁。
 * 
 * 既然是可重用的，你可以在一个线程中多次调用acquire(),在线程拥有锁时它总是返回true。
 * 
 * 你不应该在多个线程中用同一个InterProcessMutex，
 * 你可以在每个线程中都生成一个新的InterProcessMutex实例，它们的path都一样，这样它们可以共享同一个锁。
 * 
 * 
 * @author q1219331697
 *
 */
public class InterProcessMutexDemo {

	private InterProcessMutex lock;
	private final FakeLimitedResource resource;
	private final String clientName;

	public InterProcessMutexDemo(CuratorFramework client, String lockPath, FakeLimitedResource resource,
			String clientName) {
		this.resource = resource;
		this.clientName = clientName;
		this.lock = new InterProcessMutex(client, lockPath);
	}

	public void doWork(long time, TimeUnit unit) throws Exception {
		if (!lock.acquire(time, unit)) {
			throw new IllegalStateException(clientName + " could not acquire the lock");
		}
		try {
			System.out.println(clientName + " get the lock");
			resource.use(); // access resource exclusively
		} finally {
			System.out.println(clientName + " releasing the lock");
			lock.release(); // always release the lock in a finally block
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
							final InterProcessMutexDemo example = new InterProcessMutexDemo(client, PATH, resource,
									"Client " + index);
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
	}

}
