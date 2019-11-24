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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 
 * 自定义 AQS Lock
 * 
 * <p>
 * MyLock实现lock接口；
 * 
 * MyLock内部类继承AQS；
 * 若仅实现排他锁，仅覆盖 isHeldExclusively()，tryAcquire()，tryRelease() 3个方法即可；
 * 
 * AQS 模板方法模式。
 * </p>
 * 
 * <p>
 * </p>
 * 
 * @author q1219331697
 *
 */
public class MyAQSLockDemo {

	public static void main(String[] args) {
		// 测试锁
		new Thread(new Sub()).start();
		new Thread(new Sub()).start();
		new Thread(new Sub()).start();
		new Thread(new Sub()).start();

		new Thread(new Sub()).start();
		new Thread(new Sub()).start();
	}

	/**
	 * 业务中使用锁
	 */
	private static class Sub implements Runnable {
		private static Lock lock = new MyLock();

		public Sub() {
			System.out.println(lock);
		}

		@Override
		public void run() {
			lock.lock();
			try {
				try {
					// 模拟业务执行
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread() + " " + lock + " " + System.currentTimeMillis());
			} finally {
				lock.unlock();
			}
		}

	}

	/**
	 * 自定义锁
	 */
	private static class MyLock implements Lock {

		private final Sync sync;

		public MyLock() {
			sync = new Sync();
		}

		@Override
		public void lock() {
			// 加锁
			sync.acquire(1);
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			// 加锁,可响应中断
			sync.acquireInterruptibly(1);
		}

		@Override
		public boolean tryLock() {
			// 尝试加锁
			return sync.tryAcquire(1);
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// 加锁，可超时
			return sync.tryAcquireNanos(1, unit.toNanos(time));
		}

		@Override
		public void unlock() {
			// 解锁
			sync.release(1);
		}

		@Override
		public Condition newCondition() {
			// 获取Condition对象
			return sync.newCondition();
		}

		@Override
		public String toString() {
			Thread o = sync.getOwner();
			// Thread o = Thread.currentThread();
			return super.toString() + ((o == null) ? "[Unlocked]" : "[Locked by thread " + o.getName() + "]");
		}

		private static class Sync extends AbstractQueuedSynchronizer {

			private static final long serialVersionUID = 1L;

			/**
			 * 尝试获取锁，将状态从0修改为1，修改成功则将自身线程设置为当前拥有锁的线程
			 */
			@Override
			protected boolean tryAcquire(int arg) {
				if (compareAndSetState(0, 1)) {
					// 独占模式,排他锁
					setExclusiveOwnerThread(Thread.currentThread());
					return true;
				}
				return false;
			}

			/**
			 * 释放锁，将状态修改为0，因为只有一个线程持有锁，因此不需要CAS，是线程安全的
			 */
			@Override
			protected boolean tryRelease(int arg) {
				if (getState() == 0) {
					throw new UnsupportedOperationException();
				}
				setExclusiveOwnerThread(null);
				setState(0);
				return true;
			}

			// @Override
			// protected int tryAcquireShared(int arg) {
			// return super.tryAcquireShared(arg);
			// }

			// @Override
			// protected boolean tryReleaseShared(int arg) {
			// return super.tryReleaseShared(arg);
			// }

			/**
			 * state 表示获取到锁 state=1 获取到了锁，state=0，表示这个锁当前没有线程拿到
			 */
			@Override
			protected boolean isHeldExclusively() {
				return getState() == 1;
			}

			public Condition newCondition() {
				return new ConditionObject();
			}

			final Thread getOwner() {
				return getState() == 0 ? null : getExclusiveOwnerThread();
			}

		} // class Sync end

	}// class MyLock end
}
