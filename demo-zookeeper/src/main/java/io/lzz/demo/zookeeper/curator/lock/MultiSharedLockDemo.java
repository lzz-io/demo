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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 多共享锁对象 —Multi Shared Lock
 * Multi Shared Lock是一个锁的容器。 当调用acquire()，
 * 所有的锁都会被acquire()，如果请求失败，所有的锁都会被release。
 * 同样调用release时所有的锁都被release(失败被忽略)。
 * 基本上，它就是组锁的代表，在它上面的请求释放操作都会传递给它包含的所有的锁。
 * 
 * 主要涉及两个类：
 * 
 * InterProcessMultiLock
 * InterProcessLock
 * 它的构造函数需要包含的锁的集合，或者一组ZooKeeper的path。
 * 
 * 
 * 新建一个InterProcessMultiLock， 包含一个重入锁和一个非重入锁。
 * 调用acquire()后可以看到线程同时拥有了这两个锁。
 * 调用release()看到这两个锁都被释放了。
 * 
 * 
 * @author q1219331697
 *
 */
public class MultiSharedLockDemo {

	private static final String PATH1 = "/examples/locks1";
	private static final String PATH2 = "/examples/locks2";

	public static void main(String[] args) throws Exception {
		FakeLimitedResource resource = new FakeLimitedResource();
		try {
			CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
					new ExponentialBackoffRetry(1000, 3));
			client.start();

			InterProcessLock lock1 = new InterProcessMutex(client, PATH1);
			InterProcessLock lock2 = new InterProcessSemaphoreMutex(client, PATH2);

			InterProcessMultiLock lock = new InterProcessMultiLock(Arrays.asList(lock1, lock2));

			if (!lock.acquire(10, TimeUnit.SECONDS)) {
				throw new IllegalStateException("could not acquire the lock");
			}
			System.out.println("has got all lock");

			System.out.println("has got lock1: " + lock1.isAcquiredInThisProcess());
			System.out.println("has got lock2: " + lock2.isAcquiredInThisProcess());

			try {
				resource.use(); // access resource exclusively
			} finally {
				System.out.println("releasing the lock");
				lock.release(); // always release the lock in a finally block
			}
			System.out.println("has got lock1: " + lock1.isAcquiredInThisProcess());
			System.out.println("has got lock2: " + lock2.isAcquiredInThisProcess());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
