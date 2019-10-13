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
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.lzz.demo.zookeeper.config.Constants;

/**
 * 
 * 双栅栏—DistributedDoubleBarrier
 * 双栅栏允许客户端在计算的开始和结束时同步。
 * 当足够的进程加入到双栅栏时，进程开始计算， 当计算完成时，离开栅栏。
 * 双栅栏类是DistributedDoubleBarrier。
 * 构造函数为:
 * 
 * public DistributedDoubleBarrier(CuratorFramework client,
 * String barrierPath,
 * int memberQty)
 * Creates the barrier abstraction. memberQty is the number of members in the
 * barrier. When enter() is called, it blocks until
 * all members have entered. When leave() is called, it blocks until all members
 * have left.
 * 
 * Parameters:
 * client - the client
 * barrierPath - path to use
 * memberQty - the number of members in the barrier
 * 
 * memberQty是成员数量，当enter()方法被调用时，成员被阻塞，
 * 直到所有的成员都调用了enter()。
 * 
 * 当leave()方法被调用时，它也阻塞调用线程，直到所有的成员都调用了leave()。
 * 就像百米赛跑比赛， 发令枪响，
 * 所有的运动员开始跑，等所有的运动员跑过终点线，比赛才结束。
 * 
 * DistributedDoubleBarrier会监控连接状态，
 * 当连接断掉时enter()和leave()方法会抛出异常。
 * 
 * 
 * 
 * @author q1219331697
 *
 */
public class DistributedDoubleBarrierDemo {

	private static final int QTY = 5;
	private static final String PATH = "/examples/barrier";

	public static void main(String[] args) throws Exception {
		try {
			CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZK_HOST,
					new ExponentialBackoffRetry(1000, 3));
			client.start();

			ExecutorService service = Executors.newFixedThreadPool(QTY);
			for (int i = 0; i < QTY; ++i) {
				final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, QTY);
				final int index = i;
				Callable<Void> task = () -> {

					Thread.sleep((long) (3 * Math.random()));
					System.out.println("Client #" + index + " enters");
					barrier.enter();
					System.out.println("Client #" + index + " begins");
					Thread.sleep((long) (3000 * Math.random()));
					barrier.leave();
					System.out.println("Client #" + index + " leave");
					return null;
				};
				service.submit(task);
			}

			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);

			// Thread.sleep(Integer.MAX_VALUE);
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}