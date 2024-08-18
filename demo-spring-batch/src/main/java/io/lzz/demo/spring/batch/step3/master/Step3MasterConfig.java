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

import javax.jms.ConnectionFactory;

/**
 * @author q1219331697
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class Step3MasterConfig {

	@Autowired
	private Step3MasterExecutionListener step3MasterExecutionListener;

	@Bean
	public MessageChannel step3MasterOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow step3MasterOutboundFlow(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setMessageConverter(new MappingJackson2MessageConverter());
		return IntegrationFlows.from(step3MasterOutputChannel())//
				.handle(Jms.outboundAdapter(connectionFactory)//
						.destination("batch.step3.master2worker"))//
				.get();
	}

	/*
	 * Configure inbound flow (replies coming from workers)
	 */
	@Bean
	public MessageChannel step3MasterInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlows//
				.from(Jms.messageDrivenChannelAdapter(connectionFactory)//
						.destination("batch.step3.worker2master"))//
				.channel(step3MasterInputChannel())//
				.get();
	}

	@Bean
	public Step step3MasterStep(RemotePartitioningMasterStepBuilderFactory masterStepBuilderFactory,
								TaskExecutor batchTaskExecutor) {
		return masterStepBuilderFactory.get("step3MasterStep")//
				// .transactionManager(transactionManager)//
				.partitioner("step3WorkerStep", new BasicPartitioner())//
				.gridSize(3)//
				.outputChannel(step3MasterOutputChannel())//
				// 1、不设置inputChannel则为作业存储库轮询方式
				// 2、设置inputChannel则为回复聚合方式
				.inputChannel(step3MasterInputChannel()).listener(step3MasterExecutionListener)//
				// .taskExecutor(batchTaskExecutor)//
				// .throttleLimit(8)// 最大使用线程池数目
				// .allowStartIfComplete(true)//
				.build();
	}

}
