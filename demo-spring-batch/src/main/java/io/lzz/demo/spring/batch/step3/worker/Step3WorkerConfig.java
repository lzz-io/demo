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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.messaging.MessageChannel;

import io.lzz.demo.spring.batch.entity.User;

/**
 * @author q1219331697
 *
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class Step3WorkerConfig {

	private static final Logger log = LoggerFactory.getLogger(Step3WorkerConfig.class);

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Autowired
	private Step3WorkerItemReadListener step3WorkerItemReadListener;
	@Autowired
	private Step3WorkerItemWriteListener step3WorkerItemWriteListener;
	@Autowired
	private Step3WorkerExecutionListener step3WorkerExecutionListener;

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

	/*
	 * Configure outbound flow (replies going to the master)
	 */
	@Bean
	public MessageChannel step3WorkOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlows//
				.from(step3WorkOutputChannel())//
				.handle(Jms.outboundAdapter(connectionFactory)//
						.destination("batch.step3.worker2master"))//
				.get();
	}

	// ItemReader
	@Bean
	public ItemReader<User> step3WorkerItemReader() {
		JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
		reader.setName("step3WorkerItemReader");
		reader.setDataSource(dataSource);
		reader.setQueryProvider(step3WorkerQueryProvider());
		reader.setRowMapper(new BeanPropertyRowMapper<>(User.class));
		reader.setPageSize(5);
		return reader;
	}

	@Bean
	public PagingQueryProvider step3WorkerQueryProvider() {
		SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();

		provider.setDataSource(dataSource);
		provider.setSelectClause("select id, username, createTime");
		provider.setFromClause("from tb_user");
		Map<String, Order> sortKeys = new LinkedHashMap<>();
		sortKeys.put("id", Order.ASCENDING);
		provider.setSortKeys(sortKeys);

		try {
			return provider.getObject();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	// Processor
	// @Bean
	public ItemProcessor<User, User> step3WorkerItemProcessor() {
		return new Step3WorkerItemProcessor();
	}

	// ItemWriter
	// @Bean
	public ItemWriter<User> step3WorkerItemWriter() {
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
		writer.setName("step3WorkerItemWriter");

		Resource resource = new FileSystemResource("tmp/step3.txt");
		writer.setResource(resource);
		// writer.setAppendAllowed(true);
		writer.setEncoding("UTF-8");

		BeanWrapperFieldExtractor<User> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] { "id", "username", "createTime" });
		DelimitedLineAggregator<User> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setFieldExtractor(fieldExtractor);
		writer.setLineAggregator(lineAggregator);
		return writer;
	}

	/*
	 * Configure the worker step
	 */
	@Bean
	public Step step3WorkerStep(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory) {
		return workerStepBuilderFactory.get("step3WorkerStep")//
				.inputChannel(step3WorkInputChannel())//
				.outputChannel(step3WorkOutputChannel())//
				.<User, User>chunk(5)//
				.reader(step3WorkerItemReader())//
				.processor(step3WorkerItemProcessor())//
				.writer(step3WorkerItemWriter())//
				.listener(step3WorkerItemReadListener)//
				.listener(step3WorkerItemWriteListener)//
				.listener(step3WorkerExecutionListener)//
				.build();
	}

}
