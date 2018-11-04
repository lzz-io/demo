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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author q1219331697
 *
 */
@SpringBootTest(classes = TopicConsumerByMessageListener.class)
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
public class TopicConsumerByMessageListenerTest {

	private static final Logger log = LoggerFactory.getLogger(TopicConsumerByMessageListenerTest.class);

	@Autowired
	private TopicConsumerByMessageListener topicConsumerByMessageListener;

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void ContextLoader() throws Exception {
		TopicConsumerByMessageListener topicConsumerByMessageListener2 = applicationContext
				.getBean(TopicConsumerByMessageListener.class);
		if (topicConsumerByMessageListener.equals(topicConsumerByMessageListener2)) {
			log.info("seccess!");
		}

		Thread.sleep(100000);
	}

}
