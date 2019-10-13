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

package io.lzz.demo.zookeeper.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 
 * 原生api，一般不用
 * 缺点：
 * 1）连接的创建是异步的，需要开发人员自行编码实现等待 
 * 2）连接没有超时自动的重连机制 
 * 3）Zookeeper本身没提供序列化机制，需要开发人员自行指定，从而实现数据的序列化和反序列化 
 * 4）Watcher注册一次只会生效一次,需要不断的重复注册 
 * 5）Watcher的使用方式不符合java本身的术语，如果采用监听器方式，更容易理解 
 * 6）不支持递归创建树形节点
 * 
 * 
 * @author q1219331697
 *
 */
public class ZookeeperDemo {

	public static void main(String[] args) throws Exception {
		String connectString = "localhost:2181";
		ZooKeeper zookeeper = new ZooKeeper(connectString, 300, null);

		System.out.println("=========创建节点===========");
		if (zookeeper.exists("/test", false) == null) {
			zookeeper.create("/test", "znode1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}

		System.out.println("=============查看节点是否安装成功===============");
		System.out.println(new String(zookeeper.getData("/test", false, null)));

		System.out.println("=========修改节点的数据==========");
		String data = "zNode2";
		zookeeper.setData("/test", data.getBytes(), -1);

		System.out.println("========查看修改的节点是否成功=========");
		System.out.println(new String(zookeeper.getData("/test", false, null)));

		System.out.println("=======删除节点==========");
		zookeeper.delete("/test", -1);

		System.out.println("==========查看节点是否被删除============");
		System.out.println("节点状态：" + zookeeper.exists("/test", false));

		zookeeper.close();
	}
}
