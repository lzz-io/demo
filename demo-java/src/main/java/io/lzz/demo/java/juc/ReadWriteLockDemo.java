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

package io.lzz.demo.java.juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * juc ReadWriteLock 使用
 * <br>
 * <b>必须先写 try finally 结构
 * <br>
 * 注意： 最好不要把获取锁的过程写在try语句块中，
 * 因为如果在获取锁时发生了异常，异常抛出的同时也会导致锁无法被释放。</b>
 * <p>
 * 
 * <p>
 * ReadWriteLock接口的实现类-ReentrantReadWriteLock读写锁就是为了解决这个问题。
 * <br>
 * 
 * 读写锁维护了两个锁，一个是读操作相关的锁也成为共享锁，
 * 一个是写操作相关的锁 也称为排他锁。
 * 通过分离读锁和写锁，其并发性比一般排他锁有了很大提升。
 * <br>
 * 
 * 多个读锁之间不互斥，读锁与写锁互斥，写锁与写锁互斥（只要出现写操作的过程就是互斥的）。
 * 在没有线程Thread进行写入操作时，进行读取操作的多个Thread都可以获取读锁，
 * 而进行写入操作的Thread只有在获取写锁后才能进行写入操作。
 * 即多个Thread可以同时进行读取操作，但是同一时刻只允许一个Thread进行写入操作。
 * </p>
 * 
 * <pre>
 * ReentrantReadWriteLock的特性：
 * 
 * 特性
 * 说明
 * 
 * 公平性选择
 * 支持非公平（默认）和公平的锁获取方式，吞吐量上来看还是非公平优于公平
 * 
 * 重进入
 * 该锁支持重进入，以读写线程为例：读线程在获取了读锁之后，能够再次获取读锁。
 * 而写线程在获取了写锁之后能够再次获取写锁也能够同时获取读锁
 * 
 * 锁降级
 * 遵循获取写锁、获取读锁再释放写锁的次序，写锁能够降级称为读锁
 * </pre>
 * 
 * <pre>
 * ReentrantReadWriteLock常见方法：
 * 构造方法
 * 
 * 方法名称
 * 描述
 * 
 * ReentrantReadWriteLock()
 * 创建一个 ReentrantReadWriteLock()的实例
 * 
 * ReentrantReadWriteLock(boolean fair)
 * 创建一个特定锁类型（公平锁/非公平锁）的ReentrantReadWriteLock的实例
 * </pre>
 * 
 * 
 * @author q1219331697
 *
 */
public class ReadWriteLockDemo {

	public static void main(String[] args) throws Exception {
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

		// 读读共享，基本同时执行
		new Thread(new Read(readWriteLock)).start();
		new Thread(new Read(readWriteLock)).start();
		new Thread(new Read(readWriteLock)).start();
		new Thread(new Read(readWriteLock)).start();

		// 写写互斥，同一时刻只有一个线程执行
		new Thread(new Write(readWriteLock)).start();
		new Thread(new Write(readWriteLock)).start();

		// 读写互斥，同一时刻只有一个线程执行; 只有写线程拿到锁执行的过程中才会互斥
		new Thread(new Read(readWriteLock)).start();
		Thread.sleep(10);
		new Thread(new Write(readWriteLock)).start();

		// 写读互斥，同一时刻只有一个线程执行; 只有写线程拿到锁执行的过程中才会互斥
		new Thread(new Write(readWriteLock)).start();
		Thread.sleep(10);
		new Thread(new Read(readWriteLock)).start();

	}

	private static class Read implements Runnable {
		private Lock lock;

		public Read(ReadWriteLock readWriteLock) {
			this.lock = readWriteLock.readLock();
		}

		@Override
		public void run() {
			lock.lock();
			try {
				System.out.println(Thread.currentThread() + " 获取读锁 Read " + lock + " " + System.currentTimeMillis());
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

	}

	private static class Write implements Runnable {
		private Lock lock;

		public Write(ReadWriteLock readWriteLock) {
			this.lock = readWriteLock.writeLock();
		}

		@Override
		public void run() {
			lock.lock();
			try {
				System.out.println(Thread.currentThread() + " 获取写锁 Write " + lock + " " + System.currentTimeMillis());
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

	}

}
