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

package io.lzz.demo.spring.batch.step3.worker;

import javax.jms.ConnectionFactory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.MessageChannel;

/**
 * @author q1219331697
 *
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class Step3WorkerConfig {

	/*
	 * Configure inbound flow (requests coming from the master)
	 */
	@Bean
	public MessageChannel step3WorkInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow step3WorkInboundFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlows//
				.from(Jms.messageDrivenChannelAdapter(connectionFactory)//
						.destination("batch.step3.master2worker"))
				.channel(step3WorkInputChannel())//
				.get();
	}

	@Bean
	@StepScope
	public Tasklet tasklet(@Value("#{stepExecutionContext['partition']}") String partition) {
		return (contribution, chunkContext) -> {
			System.out.println("processing " + partition);
			return RepeatStatus.FINISHED;
		};
	}

	/*
	 * Configure the worker step
	 */
	@Bean
	public Step step3WorkerStep(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory) {
		return workerStepBuilderFactory.get("step3WorkerStep")//
				.inputChannel(step3WorkInputChannel())//
				.tasklet(tasklet(null))//
				.build();
	}

}
