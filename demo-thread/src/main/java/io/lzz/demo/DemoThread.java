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

package io.lzz.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

/**
 * @author q1219331697
 *
 */
public class DemoThread implements CommandLineRunner {

	private List<Future<?>> resultList = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoThread.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// ExecutorService executorService = Executors.newCachedThreadPool();
		// ExecutorService executorService = Executors.newFixedThreadPool(200);
		ExecutorService executorService = new ThreadPoolExecutor(200, 1000, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		for (int i = 0; i < 1000; i++) {
			// new Thread(new Count()).start();
			// Thread.sleep(100);
			// executorService.execute(new Count());//execute 没有返回值
			Future<?> future = executorService.submit(new Count());
			// 将任务执行结果存储到List中
			resultList.add(future);
			// System.out.println(Thread.currentThread().getName() + ":" +
			// Thread.currentThread().getId() + ":"
			// + Thread.currentThread().getThreadGroup());
		}

		executorService.shutdown();

		// 遍历任务的结果
//		for (Future<?> fs : resultList) {
//			try {
//				System.out.println(fs.get()); // 打印各个线程（任务）执行的结果
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				executorService.shutdownNow();
//				e.printStackTrace();
//				return;
//			}
//		}
	}

}
