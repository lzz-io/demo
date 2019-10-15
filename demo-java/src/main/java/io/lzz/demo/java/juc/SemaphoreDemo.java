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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Semaphore（信号量）通过构造函数设定一定数目的许可，通过acquire()方法获得许可，release()释放许可。
 * Semaphore常用来限制访问某些特定资源的线程数。
 * Semaphore初始化为只有一个许可时，称为binary semaphore，变成了一种互斥锁。
 * Semaphore可以通过构造器参数设置成公平和非公平模式。
 * 
 * semaphore.acquire() // 获取
 * semaphore.release() // 释放
 * 
 * Semaphore还有一个tryAcquire，它允许线程尝试去获取1个许可证，
 * 如果许可证不足没有获取到的话，线程也会继续执行，而非阻塞等待。
 * tryAcquire方法的重载方法tryAcquire(long timeout, TimeUnit unit)
 * 可以指定尝试获取许可证的超时时间。
 * 
 * release() 方法调用前不一定要调用 acquire() 方法
 * release() 方法会增加许可数, 不会考虑 Semaphore 构造函数中的初始许可数
 * 在调用 acquire() 方法发生 InterruptedException 异常后最好不要调用 release() 方法
 * 
 * 
 * 
 * 
 * acquire() 获取一个许可证，可以被打断，没有足够的许可证时阻塞等待
 * acquire(int permits) 获取指定数量的许可证，可以被打断，没有足够的许可证时阻塞等待
 * acquireUninterruptibly() 获取一个许可证，不可被打断，没有足够的许可证时阻塞等待
 * acquireUninterruptibly(int permits) 获取指定数量的许可证，不可被打断，没有足够的许可证时阻塞等待
 * tryAcquire() 尝试获取一个许可证，没有足够的许可证时程序继续执行，不会被阻塞
 * tryAcquire(int permits) 尝试获取指定数量的许可证，没有足够的许可证时程序继续执行，不会被阻塞
 * tryAcquire(long timeout, TimeUnit unit) 在指定的时间范围内尝试获取1个许可证，没有足够的许可证时程序继续执行，
 * 不会被阻塞，在该时间方位内可以被打断
 * tryAcquire(int permits, long timeout, TimeUnit unit)
 * 在指定的时间范围内尝试获取指定数量的许可证，没有足够的许可证时程序
 * 继续执行，不会被阻塞，在该时间方位内可以被打断
 * release() 释放一个许可证
 * drainPermits() 一次性获取所有可用的许可证
 * availablePermits() 获取当前可用许可证数量的预估值
 * hasQueuedThreads() 判断是否有处于等待获取许可证状态的线程
 * getQueueLength() 获取处于等待获取许可证状态的线程的数量的预估值
 * getQueuedThreads() 获取处于等待获取许可证状态的线程集合
 * 
 * 
 * 
 * @author q1219331697
 *
 */
public class SemaphoreDemo {

	private static final Logger log = LoggerFactory.getLogger(SemaphoreDemo.class);

	private static Semaphore semaphore = new Semaphore(4);

	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(8);

		for (int i = 0; i < 20; i++) {
			threadPool.submit(new SubThread());
		}

		threadPool.shutdown();
		// log.info("main 结束");
	}

	private static class SubThread implements Runnable {

		@Override
		public void run() {

			try {
				// 获取一个信号
				semaphore.acquire();
				// 批量获取
				// semaphore.acquire(2);
				// if (semaphore.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
				log.info("获取--信号，可用许可 {}", semaphore.availablePermits());

				Thread.sleep(3000);
				log.info("线程执行结束");
				// }

				semaphore.release();
				log.info("释放+信号，可用许可 {}", semaphore.availablePermits());
				// semaphore.release(2);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}

		}

	}
}
