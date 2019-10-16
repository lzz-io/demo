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

import java.util.concurrent.CyclicBarrier;

/**
 * 
 * 
 * 构造方法
 * public CyclicBarrier(int parties)
 * public CyclicBarrier(int parties, Runnable barrierAction)
 * 
 * 
 * 阻塞
 * public int await()
 * public int await(long timeout, TimeUnit unit)
 * 
 * await() 方法没被调用一次，计数便会减少1，并阻塞住当前线程。当计数减至0时，阻塞解除，所有在此 CyclicBarrier
 * 上面阻塞的线程开始运行。
 * 
 * 
 * 与CountDownLatch的不同之处
 * 
 * CountDownLatch对象的作用只能使用一次，当计算减为0之后就不能再使用了。
 * 但CyclicBarrier对象可以重复使用。**在计算减为0后，如果再次调用await() 方法，计数就又会变成
 * N-1，新一轮重新开始，也可以通过reset()重置，这便是 Cyclic 的含义所在。
 * 
 * 不用向CountDownLatch一样调用countDown（）方法
 * 
 * CountDownLatch是 一组线程等待另一组线程；
 * CyclicBarrier是一组线程互相等待。
 * 
 * 
 * CyclicBarrier 的构造函数还可以接受一个 Runnable，会在所有得线程都到达同步点后执行某些操作。
 * 
 * 
 * CyclicBarrier.await() 方法会抛出一个独有的 BrokenBarrierException。这个异常发生在当某个线程在等待本
 * CyclicBarrier 时被中断或超时或被重置时，其它同样在这个 CyclicBarrier 上等待的线程便会受到
 * BrokenBarrierException。意思就是说，同志们，别等了，有个小伙伴已经挂了，
 * 咱们如果继续等有可能会一直等下去，所有各回各家吧。
 * 
 * 
 * @author q1219331697
 *
 */
public class CyclicBarrierDemo {

	// private static CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
	private static CyclicBarrier cyclicBarrier;

	public static void main(String[] args) throws Exception {

		// cyclicBarrier = new CyclicBarrier(5);
		cyclicBarrier = new CyclicBarrier(5, () -> {
			System.out.println("所有线程准备就绪 ...");
		});

		for (int i = 0; i < 10; i++) {
			System.out.println(Thread.currentThread().getName() + " 创建线程 " + i + " ...");
			new Thread(new SubThread()).start();
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " 创建线程 " + i + " 结束...");
		}
	}

	private static class SubThread implements Runnable {

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " 达到...");
			try {
				cyclicBarrier.await();
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " 执行完成...");
		}

	}
}
