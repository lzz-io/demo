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

package io.lzz.demo.zookeeper.curator.barrier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 分布式屏障—Barrier
 * 分布式Barrier是这样一个类： 它会阻塞所有节点上的等待进程，
 * 直到某一个被满足， 然后所有的节点继续进行。
 * 
 * 比如赛马比赛中， 等赛马陆续来到起跑线前。 一声令下，所有的赛马都飞奔而出。
 * 
 * 
 * DistributedBarrier
 * DistributedBarrier类实现了栅栏的功能。
 * 
 * 它的构造函数如下：
 * public DistributedBarrier(CuratorFramework client, String barrierPath)
 * Parameters:
 * client - client
 * barrierPath - path to use as the barrier
 * 
 * 
 * 首先你需要设置栅栏，它将阻塞在它上面等待的线程:
 * setBarrier();
 * 
 * 
 * 然后需要阻塞的线程调用方法等待放行条件:
 * public void waitOnBarrier()
 * 
 * 
 * 当条件满足时，移除栅栏，所有等待的线程将继续执行：
 * removeBarrier();
 * 
 * 
 * 异常处理 DistributedBarrier 会监控连接状态，
 * 当连接断掉时waitOnBarrier()方法会抛出异常。
 * 
 * 
 * 异常处理 DistributedBarrier 会监控连接状态，
 * 当连接断掉时waitOnBarrier()方法会抛出异常。
 * 
 * 
 * 这个例子创建了controlBarrier来设置栅栏和移除栅栏。
 * 我们创建了5个线程，在此Barrier上等待。
 * 最后移除栅栏后所有的线程才继续执行。
 * 
 * 如果你开始不设置栅栏，所有的线程就不会阻塞住。
 * 
 * 
 * @author q1219331697
 *
 */
public class DistributedBarrierDemo {

	private static final int QTY = 5;
	private static final String PATH = "/examples/barrier";

	public static void main(String[] args) throws Exception {
		try {
			CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
					new ExponentialBackoffRetry(1000, 3));
			client.start();
			ExecutorService service = Executors.newFixedThreadPool(QTY);
			DistributedBarrier controlBarrier = new DistributedBarrier(client, PATH);
			controlBarrier.setBarrier();

			for (int i = 0; i < QTY; ++i) {
				final DistributedBarrier barrier = new DistributedBarrier(client, PATH);
				final int index = i;
				Callable<Void> task = () -> {
					Thread.sleep((long) (3 * Math.random()));
					System.out.println("Client #" + index + " waits on Barrier");
					barrier.waitOnBarrier();
					System.out.println("Client #" + index + " begins");
					return null;
				};
				service.submit(task);
			}

			System.out.println("all Barrier instances should wait the condition");
			Thread.sleep(5000);

			// 放行
			controlBarrier.removeBarrier();

			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);

			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
