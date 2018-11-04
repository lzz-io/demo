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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.lzz.demo.jms.queue.QueueConsumer;
import io.lzz.demo.jms.topic.TopicConsumerByReceive;

/**
 * @author q1219331697
 *
 */
@SpringBootApplication
public class JmsNoSpringApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(JmsNoSpringApplication.class);

	@Autowired
	private QueueConsumer queueConsumer;

	@Autowired
	private TopicConsumerByReceive topicConsumerByReceive;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JmsNoSpringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		for (int i = 0; i < 2; i++) {
			log.info("begin queue recive...");
			new Thread(() -> {
				try {
					queueConsumer.doRec();
				} catch (Exception e) {
					log.error("{}", e);
				}
			}).start();
			log.info("end queue recive...");

			log.info("begin topic recive...");
			new Thread(() -> {
				try {
					topicConsumerByReceive.doReceive();
				} catch (Exception e) {
					log.error("{}", e);
				}
			}).start();
			log.info("end topic recive...");
		}

	}

}
