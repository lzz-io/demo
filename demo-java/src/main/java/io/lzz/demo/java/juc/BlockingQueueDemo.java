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

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 阻塞队列提供了四种处理方法:
 * 
 * 方法\处理方式 抛出异常 返回特殊值 一直阻塞 超时退出
 * 插入方法 add(e) offer(e) put(e) offer(e,time,unit)
 * 移除方法 remove() poll() take() poll(time,unit)
 * 检查方法 element() peek() 不可用 不可用
 * 
 * 抛出异常：是指当阻塞队列满时候，再往队列里插入元素，会抛出 IllegalStateException("Queue full")
 * 异常。当队列为空时，从队列里获取元素时会抛出 NoSuchElementException 异常 。
 * 
 * 返回特殊值：插入方法会返回是否成功，成功则返回 true。移除方法，则是从队列里拿出一个元素，如果没有则返回 null
 * 
 * 一直阻塞：当阻塞队列满时，如果生产者线程往队列里 put
 * 元素，队列会一直阻塞生产者线程，直到拿到数据，或者响应中断退出。
 * 当队列空时，消费者线程试图从队列里 take 元素，队列也会阻塞消费者线程，直到队列可用。
 * 
 * 超时退出：当阻塞队列满时，队列会阻塞生产者线程一段时间，如果超过一定的时间，生产者线程就会退出。
 * 
 * 
 * 
 * JDK7 提供了 7 个阻塞队列。分别是
 * 
 * ArrayBlockingQueue ：一个由数组结构组成的有界阻塞队列。
 * LinkedBlockingQueue ：一个由链表结构组成的有界阻塞队列。
 * PriorityBlockingQueue ：一个支持优先级排序的无界阻塞队列。
 * DelayQueue：一个使用优先级队列实现的无界阻塞队列。
 * SynchronousQueue：一个不存储元素的阻塞队列。
 * LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
 * LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
 * 
 * LinkedBlockingQueue性能最好
 * ArrayBlockingQueue能实现公平锁，一般也没啥场景用
 * 
 * 
 * 
 * 核心方法
 * 一直阻塞
 * put(E e);
 * take();
 * 
 * 带超时时间
 * offer(E e, long timeout, TimeUnit unit)，
 * poll(long timeout, TimeUnit unit)；
 * 
 * 
 * offer(E e): 将给定的元素设置到队列中，如果设置成功返回true, 否则返回false. e的值不能为空，否则抛出空指针异常。
 * offer(E e, long timeout, TimeUnit unit): 将给定元素在给定的时间内设置到队列中，如果设置成功返回true,
 * 否则返回false.
 * add(E e): 将给定元素设置到队列中，如果设置成功返回true, 否则抛出异常。如果是往限定了长度的队列中设置值，推荐使用offer()方法。
 * 
 * put(E e): 将元素设置到队列中，如果队列中没有多余的空间，该方法会一直阻塞，直到队列中有多余的空间。
 * take(): 从队列中获取值，如果队列中没有值，线程会一直阻塞，直到队列中有值，并且该方法取得了该值。
 * poll(long timeout, TimeUnit unit):
 * 获取并移除此队列的头元素，可以在指定的等待时间前等待可用的元素，timeout表明放弃之前要等待的时间长度，用 unit
 * 的时间单位表示，如果在元素可用前超过了指定的等待时间，则返回null，当等待时可以被中断
 * remainingCapacity()：获取队列中剩余的空间。
 * remove(Object o): 从队列中移除指定的值。
 * contains(Object o): 判断队列中是否拥有该值。
 * drainTo(Collection c): 将队列中值，全部移除，并发设置到给定的集合中。
 * 
 * 
 * 
 * @author q1219331697
 */
public class BlockingQueueDemo {

	private static BlockingQueue<String> queue = new LinkedBlockingQueue<>(4);

	public static void main(String[] args) {

		new Thread(new Producer(queue)).start();
		new Thread(new Consumer(queue)).start();
		new Thread(new Consumer(queue)).start();
		new Thread(new Consumer(queue)).start();
		new Thread(new Consumer(queue)).start();

	}

	private static class Producer implements Runnable {
		private BlockingQueue<String> queue;

		public Producer(BlockingQueue<String> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			for (int i = 0; i < 100; i++) {
				try {
					String data = Integer.toString(new Random().nextInt());
					// 阻塞
					queue.put(data);
					System.out.println("添加 + : " + data);

					Thread.sleep(new Random().nextInt(100));
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private static class Consumer implements Runnable {
		private BlockingQueue<String> queue;

		public Consumer(BlockingQueue<String> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			while (true) {
				try {
					// 阻塞，超时退出
					String data = queue.poll(3000, TimeUnit.MILLISECONDS);

					// 一直阻塞
					// String data = queue.take();

					if (data == null) {
						System.out.println("未获取到数据，结束");
						break;
					}

					Thread.sleep(new Random().nextInt(1000));
					System.out.println("获取 -- : " + data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
