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

package io.lzz.demo.jms.queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author q1219331697
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = QueueProducer.class)
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) 指定随机端口
// @WebMvcTest 与 @SpringBootTest 注解冲突，
// 如果需要使用 MockMvc，使用 @AutoConfigureMockMvc 注解
// @AutoConfigureMockMvc
@EnableAutoConfiguration
public class QueueProducerTest {

    private static final Logger logger = LoggerFactory.getLogger(QueueProducerTest.class);

    @Autowired
    private QueueProducer queueSender;

    @Test
    public void testDoSend() throws InterruptedException, ExecutionException {
        queueSender.doSend();
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 1000; i++) {
            newFixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    queueSender.doSend();
                }
            });
        }

        newFixedThreadPool.shutdown();
        logger.info("{}", newFixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS));
    }

}
