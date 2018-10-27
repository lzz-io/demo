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

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author q1219331697
 *
 */
@Service
public class QueueSender {

	private static final Logger log = LoggerFactory.getLogger(QueueSender.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	public void doSend() {
		Destination destination = new ActiveMQQueue("jms.queue");

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					jmsTemplate.convertAndSend(destination, Thread.currentThread().getName());
				}
			}, String.valueOf(i)).start();
		}

		int sessionAcknowledgeMode = jmsTemplate.getSessionAcknowledgeMode();
		
		log.info("send to {}--{}--{}", jmsTemplate.getSessionAcknowledgeMode(), Thread.currentThread().getName(),
				Thread.currentThread().getId());
	}

}
