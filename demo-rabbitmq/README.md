# demo-rabbitmq


## 可靠消息

1、发送：开启事务或者confirm。

2、rabbitmq：配置rabbitmq镜像、持久化。

3、消费端：手动ACK。



#### 发送消息

transaction事务模式和confirm模式不能共存。

##### 性能：

异步confirm模式(async) > 批量confirm模式(batch) > 普通confirm模式(common) > 事务模式(tx)。



普通confirm模式：每发送一条消息后，调用waitForConfirms()方法，等待服务器端confirm。实际上是一种串行confirm了。

批量confirm模式：每发送一批消息后，调用waitForConfirms()方法，等待服务器端confirm。

异步confirm模式：提供一个回调方法，服务端confirm了一条或者多条消息后Client端会回调这个方法。



（来源网上测试）发送平均速率：

- 事务模式（tx）：1637.484
- 普通confirm模式(common)：1936.032
- 批量confirm模式(batch)：10432.45
- 异步confirm模式(async)：10542.06

可以看到事务模式性能是最差的，普通confirm模式性能比事务模式稍微好点，但是和批量confirm模式还有异步confirm模式相比，还是小巫见大巫。批量confirm模式的问题在于confirm之后返回false之后进行重发这样会使性能降低，异步confirm模式(async)编程模型较为复杂，至于采用哪种方式，那是仁者见仁智者见智了。



### 事务模式

RabbitMQ中与事务机制有关的方法有三个：`txSelect()`, `txCommit()`以及`txRollback()`, 

txSelect用于将当前channel设置成transaction模式，

txCommit用于提交事务，

txRollback用于回滚事务

```java
channel.txSelect(); 
channel.basicPublish("", QUEUE_NAME, null, msg.getBytes()); 
channel.txCommit(); 
```



### 消息confirm

**Confirm的三种实现方式：**

方式一：channel.waitForConfirms()普通发送方确认模式；

方式二：channel.waitForConfirmsOrDie()批量确认模式；

方式三：channel.addConfirmListener()异步监听发送方确认模式；



### 消息持久化

必须满足三点，且三者缺一不可。

- 交换器必须是持久化。
- 队列必须是持久化的。
- 消息必须是持久化的。



#### 原生的实现方式

第一步，交换器的持久化。

```java
// 参数1 exchange ：交换器名
// 参数2 type ：交换器类型
// 参数3 durable ：是否持久化
channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
```

第二步，队列的持久化。

```java
// 参数1 queue ：队列名
// 参数2 durable ：是否持久化
// 参数3 exclusive ：仅创建者可以使用的私有队列，断开后自动删除
// 参数4 autoDelete : 当所有消费客户端连接断开后，是否自动删除队列
// 参数5 arguments
channel.queueDeclare(QUEUE_NAME, true, false, false, null);
```

第三步，消息的持久化。

```java
// 参数1 exchange ：交换器
// 参数2 routingKey ： 路由键
// 参数3 props ： 消息的其他参数,其中 MessageProperties.PERSISTENT_TEXT_PLAIN 表示持久化
// 参数4 body ： 消息体
channel.basicPublish("", queue_name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
```



##### BasicProperties详解

在 *AMQP* 协议中，为消息预定了 14 个属性，如下：

- *content_type*：标明消息的类型.
- *content_encoding*：标明消息的编码.
- *headers*：可扩展的信息对.
- *delivery_mode*：为 `2` 时表示该消息需要被持久化支持.
- *priority*：该消息的权重.
- *correlation_id*：用于”请求”与”响应”之间的匹配.
- *reply_to*：”响应”的目标队列.
- *expiration*：有效期.
- *message_id*：消息的ID.
- *timestamp*：一个时间戳.
- *type*：消息的类型.
- *user_id*：用户的ID.
- *app_id*：应用的ID.
- *cluster_id*：服务集群ID.


###### 常用属性：

- delivery_mode(投递模式): 将消息标记为持久化(值为2)或者暂存(除2以外的任何值).
- content_type(内容类型): 用来描述编码的mime-type.
- reply_to(回复目标): 通常用来命名回调队列.
- correlation_id(关联标识): 用来将RPC的响应和请求关联起来.

属性这么多，那到底该怎么设置呢？

在`RabiitMQ`的`MessageProperties`类中进行了一些六个`BasicProperties`配置

根据传递的信息选择，这里我们传输的消息主要为文本消息，所以使用`MessageProperties.PERSISTENT_TEXT_PLAIN`即可。

也可以自定义：

```java
AMQP.BasicProperties props = new AMQP.BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8").build();
```



#### Spring AMQP 的实现方式

Spring AMQP 是对原生的 RabbitMQ 客户端的封装。一般情况下，我们只需要定义交换器的持久化和队列的持久化。

其中，交换器的持久化配置如下。

```java
// 参数1 name ：交互器名
// 参数2 durable ：是否持久化
// 参数3 autoDelete ：当所有消费客户端连接断开后，是否自动删除队列
new TopicExchange(name, durable, autoDelete)
```

此外，还需要再配置队列的持久化。

```java
// 参数1 name ：队列名
// 参数2 durable ：是否持久化
// 参数3 exclusive ：仅创建者可以使用的私有队列，断开后自动删除
// 参数4 autoDelete : 当所有消费客户端连接断开后，是否自动删除队列
new Queue(name, durable, exclusive, autoDelete);
```

至此，RabbitMQ 的消息持久化配置完毕。



#### 消费端：手动ACK

**channel.basicAck(deliveryTag, false);**

```java
channel.basicAck(deliveryTag, false);
System.out.println("消息序号:" + envelope.getDeliveryTag());
System.out.println("交换器:" + envelope.getExchange());
System.out.println("路由键:" + envelope.getRoutingKey());
```



#### 限流qos

关键：

1、手动ACK

2、channel.basicQos(0, 1, true);

```java
// channel.basicQos(获取消息最大数[0-无限制], 依次获取数量, 作用域[true作用于整个channel，false作用于具体消费者]);
// global 设置为 true的时候，并没有了限流的作用，有文章说是没有实现此功能
channel.basicQos(0, 1, true);
```



##### TTL

设定消息的生存时间，过期消息自动删除。

​	**队列设置：**在队列申明的时候使用 x-message-ttl 参数，单位为 毫秒

​    **单个消息设置：**是设置消息属性的 expiration 参数的值，单位为 毫秒

设定：（关键expiration("10000") // 有效期）

1、在消息发送时可以进行指定。

```java
AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .expiration("10000") // 有效期
                .build();
String msg = "test message";
channel.basicPublish("", queueName, properties, msg.getBytes());
```

2、在后台管理界面中新增一个 queue，创建时可以设置 ttl，对于队列中超过该时间的消息将会被移除。



#### 死信队列&死信交换机

死信队列实际上就是一个普通的队列，只是这个队列跟死信交换机进行了绑定，用来存放死信而已。



**生产者   -->  消息（普通消息，带DL参数） --> 交换机  --> 队列  --> 变成死信  --> DLX交换机 -->队列 --> 消费者**



消息变成死信一般是以下几种情况：

1. 消息被拒绝（basic.reject/ basic.nack）并且不再重新投递 requeue=false
2. 消息超期 (rabbitmq  Time-To-Live -> messageProperties.setExpiration()) 
3. 队列超载



**如何使用死信交换机**

**定义业务（普通）队列的时候指定参数**（必须两个都设置）

- **x-dead-letter-exchange** : 用来设置死信后发送的交换机。
- **x-dead-letter-routing-key** ：用来设置死信的routingKey，设定后能在DL进入DLX后自动路由到对应的queue中。