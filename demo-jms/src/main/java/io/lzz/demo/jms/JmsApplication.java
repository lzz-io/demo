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

package io.lzz.demo.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.*;

/**
 * @author q1219331697
 */
@SpringBootApplication
@EnableJms
public class JmsApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(JmsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616?jms.prefetchPolicy.queuePrefetch=0");
        Connection connection = connectionFactory.createConnection("admin", "admin");
        connection.start();

        while (true) {
            Session session = connection.createSession(false, 2);
            ActiveMQQueue queue = new ActiveMQQueue("demo.mq.queue.test");
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(10000L);
            message.acknowledge();//手动确认
            // session.commit();
            System.out.println("session.getAcknowledgeMode()" + session.getAcknowledgeMode());
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println(Thread.currentThread() + text);
            session.close();
        }
    }
}
