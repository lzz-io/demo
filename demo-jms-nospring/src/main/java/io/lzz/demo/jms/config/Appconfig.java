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

package io.lzz.demo.jms.config;

import javax.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * @author q1219331697
 *
 */
@Configuration
public class Appconfig {

	// @Bean
	// public Connection connection(ActiveMQConnectionFactory connectionFactory)
	// throws JMSException {
	// return connectionFactory.createConnection();
	// }

	// public JmsListenerContainerFactory queueMessageListenerContainer() {
	// return null;
	// }

	@Bean
	public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setPubSubDomain(true);
		bean.setConnectionFactory(connectionFactory);
		return bean;
	}

	@Bean
	public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setPubSubDomain(false);
		bean.setConnectionFactory(connectionFactory);
		return bean;
	}

	// @Bean
	// public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
	// JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
	// jmsTemplate.setPubSubDomain(true);
	// // jmsTemplate.setExplicitQosEnabled(true);
	// // jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	// // jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
	// // jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
	// return jmsTemplate;
	// }

}
