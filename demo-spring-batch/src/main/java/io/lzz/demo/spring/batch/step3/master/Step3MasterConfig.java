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

package io.lzz.demo.spring.batch.step3.master;

import javax.jms.ConnectionFactory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.MessageChannel;

/**
 * @author q1219331697
 *
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class Step3MasterConfig {

	@Autowired
	private Step3MasterExecutionListener step3MasterExecutionListener;

	@Bean
	public MessageChannel step3MasterOutboundRequest() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow step3MasterOutboundFlow(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setMessageConverter(new MappingJackson2MessageConverter());
		return IntegrationFlows.from(step3MasterOutboundRequest())//
				.handle(Jms.outboundAdapter(connectionFactory)//
						.destination("batch.step3.master2worker"))//
				.get();
	}

	@Bean
	public Step step3MasterStep(RemotePartitioningMasterStepBuilderFactory masterStepBuilderFactory,
			TaskExecutor taskExecutor) {
		return masterStepBuilderFactory.get("step3MasterStep")//
				// .transactionManager(transactionManager)//
				.partitioner("step3WorkerStep", new BasicPartitioner())//
				.gridSize(3)//
				.outputChannel(step3MasterOutboundRequest())//
				// 1、不设置inputChannel则为作业存储库轮询方式
				// .inputChannel(inputChannel)
				.listener(step3MasterExecutionListener)//
				// .taskExecutor(taskExecutor)//
				// .throttleLimit(8)// 最大使用线程池数目
				// .allowStartIfComplete(true)//
				.build();
	}

}
