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

package io.lzz.demo.spring.batch.config;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;

import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.task.MyItemProcessor;

/**
 * @author q1219331697
 *
 */
@Configuration
public class AppConfig {

	@Bean
	public FlatFileItemReader<User> reader() {
		BeanWrapperFieldSetMapper<User> mapper = new BeanWrapperFieldSetMapper<>();
		mapper.setTargetType(User.class);
		return new FlatFileItemReaderBuilder<User>() //
				.name("reader") //
				.encoding("UTF-8") //
				.resource(new ClassPathResource("data.csv")) //
				.delimited() //
				// .quoteCharacter(',')//
				.delimiter(",")//
				.names(new String[] { "id", "username", "createTime" }) //
				.linesToSkip(1)//
				.fieldSetMapper(mapper) //
				.build();
	}

	@Bean
	public ItemProcessor<User, String> processor() {
		return new MyItemProcessor();
	}

	@Bean
	public ItemWriter<User> csvWriter() throws Exception {
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
		writer.setName("csvWriter");

		// Resource resource = new ClassPathResource("out.csv");
		Resource resource = new FileSystemResource("out/out.csv");
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

	@Bean("jmsWriter")
	public ItemWriter<String> jmsWriter(JmsTemplate jmsTemplate) {
		JmsItemWriter<String> writer = new JmsItemWriter<>();
		Destination destination = new ActiveMQQueue(Constants.BANTCH_QUEUE_TEST);
		jmsTemplate.setDefaultDestination(destination);
		// pojo转换
		// MessageConverter messageConverter = new MappingJackson2MessageConverter();
		// jmsTemplate.setMessageConverter(messageConverter);
		writer.setJmsTemplate(jmsTemplate);
		return writer;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("spring_batch");
	}

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step1) {
		return jobBuilderFactory.get("job")//
				// .preventRestart()//失败作业不能重启，默认true
				.flow(step1).end()//
				.build();
	}
}
