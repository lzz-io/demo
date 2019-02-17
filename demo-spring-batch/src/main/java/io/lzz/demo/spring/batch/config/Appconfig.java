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
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;

import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.task.MyChunkListener;
import io.lzz.demo.spring.batch.task.MyItemProcessListener;
import io.lzz.demo.spring.batch.task.MyItemReadListener;
import io.lzz.demo.spring.batch.task.MyItemWriteListener;
import io.lzz.demo.spring.batch.task.UserItemProcessor;

/**
 * @author q1219331697
 *
 */
@Configuration
public class Appconfig {

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
		return new UserItemProcessor();
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

	@Autowired
	private MyChunkListener myChunkListener;

	@Autowired
	private MyItemReadListener myItemReadListener;

	@Autowired
	private MyItemProcessListener myItemProcessListener;

	@Autowired
	private MyItemWriteListener myItemWriteListener;

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, @Qualifier("jmsWriter") ItemWriter<String> writer) {
		return stepBuilderFactory.get("step1")//
				.<User, String>chunk(1)//
				.reader(reader())//
				.processor(processor())//
				.writer(writer)//
				.listener(myItemReadListener)//
				.listener(myItemProcessListener)//
				.listener(myItemWriteListener)//
				.listener(myChunkListener)//
				.build();
	}

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, Step step1) {
		return jobBuilderFactory.get("job")//
				.flow(step1).end().build();
	}
}
