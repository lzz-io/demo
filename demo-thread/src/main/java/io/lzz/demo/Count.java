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

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author q1219331697
 *
 */
public class Count implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Count.class);

	private static int count = 0;
	private static AtomicInteger countAtomicInteger = new AtomicInteger();

	@Override
	public void run() {

		log.info(Thread.currentThread() + "+count+" + (++count));
		log.info(Thread.currentThread() + "+countAtomicInteger+" + (countAtomicInteger.incrementAndGet()));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 制造随机异常
		if (new Random().nextBoolean()) {
			try {
				throw new Exception("随机异常！");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		log.info(Thread.currentThread() + "-count-" + (--count));
		log.info(Thread.currentThread() + "-countAtomicInteger-" + (countAtomicInteger.decrementAndGet()));
		System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().getId() + ":"
				+ Thread.currentThread().getThreadGroup());
	}

}
