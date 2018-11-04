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

package io.lzz.demo.jms.topic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.lzz.demo.jms.topic.TopicProducer;

/**
 * @author q1219331697
 *
 */
@SpringBootTest(classes = TopicProducer.class)
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
public class TopicProducerTest {

	private static final Logger log = LoggerFactory.getLogger(TopicProducerTest.class);

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testDoSendTopic() throws Exception {
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {

				try {
					mockMvc.perform(get("/topic")).andExpect(status().isOk());
				} catch (Exception e) {
					log.error("{}", e);
				}

			}).start();
		}

		// sleep 5s,让线程有时间执行完
		Thread.sleep(5000);
	}

}
