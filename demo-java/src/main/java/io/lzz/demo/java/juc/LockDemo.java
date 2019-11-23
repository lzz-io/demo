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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * juc lock 使用
 * <br>
 * <b>必须先写 try finally 结构
 * <br>
 * 注意： 最好不要把获取锁的过程写在try语句块中，
 * 因为如果在获取锁时发生了异常，异常抛出的同时也会导致锁无法被释放。</b>
 * <p>
 * Lock接口提供的synchronized关键字不具备的主要特性：
 * 
 * 特性
 * 描述
 * 
 * 尝试非阻塞地获取锁
 * 当前线程尝试获取锁，如果这一时刻锁没有被其他线程获取到，则成功获取并持有锁
 * 
 * 能被中断地获取锁
 * 获取到锁的线程能够响应中断，当获取到锁的线程被中断时，中断异常将会被抛出，同时锁会被释放
 * 
 * 超时获取锁
 * 在指定的截止时间之前获取锁， 超过截止时间后仍旧无法获取则返回
 * </P>
 * 
 * <p>
 * Lock接口基本的方法：
 * 
 * 方法名称
 * 描述
 * 
 * void lock()
 * 获得锁。如果锁不可用，则当前线程将被禁用以进行线程调度，并处于休眠状态，直到获取锁。
 * 
 * void lockInterruptibly()
 * 获取锁，如果可用并立即返回。如果锁不可用，那么当前线程将被禁用以进行线程调度，并且处于休眠状态，和lock()方法不同的是在锁的获取中可以中断当前线程（相应中断）。
 * 
 * Condition newCondition()
 * 获取等待通知组件，该组件和当前的锁绑定，当前线程只有获得了锁，才能调用该组件的wait()方法，而调用后，当前线程将释放锁。
 * 
 * boolean tryLock()
 * 只有在调用时才可以获得锁。如果可用，则获取锁定，并立即返回值为true；如果锁不可用，则此方法将立即返回值为false 。
 * 
 * boolean tryLock(long time, TimeUnit unit)
 * 超时获取锁，当前线程在一下三种情况下会返回： 1. 当前线程在超时时间内获得了锁；2.当前线程在超时时间内被中断；3.超时时间结束，返回false.
 * 
 * void unlock()
 * 释放锁。
 * </p>
 * 
 * <pre>
 * 等待通知模型
 * 一个lock可以对应多个condition
 * 
 * Condition接口的常见方法：
 * 
 * 方法名称
 * 描述
 * 
 * void await()
 * 相当于Object类的wait方法
 * 
 * boolean await(long time, TimeUnit unit)
 * 相当于Object类的wait(long timeout)方法
 * 
 * signal()
 * 相当于Object类的notify方法
 * 
 * signalAll()
 * 相当于Object类的notifyAll方法
 * </pre>
 * 
 * <pre>
 * </pre>
 * 
 * @author q1219331697
 *
 */
public class LockDemo {

	private static final Logger log = LoggerFactory.getLogger(LockDemo.class);

	public static void main(String[] args) throws Exception {

		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();

		new Thread(new Sub(lock, condition)).start();
		new Thread(new Sub(lock, condition)).start();
		new Thread(new Sub(lock, condition)).start();
		new Thread(new Sub(lock, condition)).start();

		// 放行
		// sleep 2s 防止线程还没创建出来，就已经signal了。lost wake up。
		Thread.sleep(2000);
		lock.lock();
		try {
			// 和 synchronized 一样，
			// 必须在condition.await()方法调用之前调用lock.lock()代码获得同步监视器，不然会报错。
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	private static class Sub implements Runnable {

		private Lock lock;
		private Condition condition;

		public Sub(Lock lock, Condition condition) {
			this.lock = lock;
			this.condition = condition;
		}

		@Override
		public void run() {
			lock.lock();
			try {
				System.out.println(Thread.currentThread() + " 阻塞...");
				condition.await();
				System.out.println(Thread.currentThread() + " 放行...");
			} catch (InterruptedException e) {
				log.error("", e);
				Thread.currentThread().interrupt();
			} finally {
				lock.unlock();
			}
		}

	}
}
