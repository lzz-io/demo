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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author q1219331697
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = QueueProducer.class)
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) 指定随机端口
// @WebMvcTest 与 @SpringBootTest 注解冲突，
// 如果需要使用 MockMvc，使用 @AutoConfigureMockMvc 注解
// @AutoConfigureMockMvc
@EnableAutoConfiguration
public class QueueProducerTest {

	@Autowired
	private QueueProducer queueSender;

	@Test
	public void testDoSend() throws InterruptedException {
		queueSender.doSend();
		Thread.sleep(10000);
	}

}
