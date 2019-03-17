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

package io.lzz.demo.spring.batch.step2.worker;

import javax.jms.ConnectionFactory;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.MessageChannel;

import io.lzz.demo.spring.batch.entity.User;

/**
 * RemoteChunking worker配置
 * 
 * @author q1219331697
 *
 */
@EnableBatchProcessing
@EnableBatchIntegration
@Configuration
public class Step2WorkerConfig {

	@SuppressWarnings("unused")
	@Autowired
	@Qualifier("taskExecutor")
	private TaskExecutor taskExecutor;

	/*
	 * Configure inbound flow (requests coming from the master)
	 */
	@Bean
	public MessageChannel step2WorkerInputChannel() {
		return new DirectChannel();
		// return new ExecutorChannel(taskExecutor);
	}

	@Bean
	public IntegrationFlow workerInboundFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlows//
				.from(Jms.messageDrivenChannelAdapter(connectionFactory)//
						.destination("batch.step2.master2worker"))//
				.channel(step2WorkerInputChannel())//
				.get();
	}

	/*
	 * Configure outbound flow (replies going to the master)
	 */
	@Bean
	public MessageChannel step2WorkerOutputChannel() {
		return new DirectChannel();
		// return new ExecutorChannel(taskExecutor);
	}

	@Bean
	public IntegrationFlow workerOutboundFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlows//
				.from(step2WorkerOutputChannel())//
				.handle(Jms.outboundAdapter(connectionFactory)//
						.destination("batch.step2.worker2master"))
				.get();
	}

	// Processor
	@Bean
	public ItemProcessor<User, User> step2WorkerItemProcessor() {
		return new Step2WorkerItemProcessor();
	}

	// ItemWriter
	@Bean
	public ItemWriter<User> step2WorkerItemWriter() {
		// Step2WorkerWriter<User> writer = new Step2WorkerWriter<>();
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
		writer.setName("step2WorkerItemWriter");

		Resource resource = new FileSystemResource("tmp/step2.csv");
		writer.setResource(resource);
		// writer.setAppendAllowed(true);
		writer.setEncoding("UTF-8");

		BeanWrapperFieldExtractor<User> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] { "id", "username", "createTime" });
		DelimitedLineAggregator<User> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setFieldExtractor(fieldExtractor);
		writer.setLineAggregator(lineAggregator);

		// TODO 待验证
		ExecutionContext executionContext = new ExecutionContext();
		writer.open(executionContext);
		return writer;
	}

	@Bean
	public IntegrationFlow workerFlow(RemoteChunkingWorkerBuilder<User, User> workerBuilder) {
		return workerBuilder//
				.itemProcessor(step2WorkerItemProcessor())//
				.itemWriter(step2WorkerItemWriter())//
				.inputChannel(step2WorkerInputChannel()) // requests received from the master
				.outputChannel(step2WorkerOutputChannel()) // replies sent to the master
				.build();
	}

}
