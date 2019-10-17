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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author q1219331697
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaApplicationTest {

	private static final Logger log = LoggerFactory.getLogger(KafkaApplicationTest.class);

	@Autowired
	private ApplicationContext context;

	@Test
	public void testMain() {
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		for (String string : beanDefinitionNames) {
			log.info(string);
		}
		log.info("{}", beanDefinitionNames.length);
	}

}
