/*
 * Copyright qq:1219331697
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * countDownLatch.await() 阻塞，当 countDownLatch = 0，继续执行。
 * 
 * @author q1219331697
 *
 */
public class CountDownLatchDemo {

	private static CountDownLatch countDownLatch = new CountDownLatch(10);

	public static void main(String[] args) throws Exception {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 10; i++) {
			threadPool.submit(new SubThread());
		}

		System.out.println("主线程开始");
		System.out.println("主线程等待");
		countDownLatch.await();
		System.out.println("主线程结束");

		threadPool.shutdown();
	}

	private static class SubThread implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println(Thread.currentThread() + "子线程开始 + ，当前countDownLatch：" + countDownLatch);
				Thread.sleep(500);
				countDownLatch.countDown();
				System.out.println(Thread.currentThread() + "子线程结束 -- ，当前countDownLatch：" + countDownLatch);
			} catch (Exception e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}

		}

	}
}
