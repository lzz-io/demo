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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.lzz.demo.jms.topic.TopicConsumerByJmsListenerAnnotation;

/**
 * @author q1219331697
 *
 */
@SpringBootTest(classes = TopicConsumerByJmsListenerAnnotation.class)
@RunWith(SpringRunner.class)
@FixMethodOrder
public class TopicConsumerByJmsListenerAnnotationTest {

	private static final Logger log = LoggerFactory.getLogger(TopicConsumerByJmsListenerAnnotationTest.class);

	@Test
	public void testDoRec() {
		log.info("success!");
	}

}
