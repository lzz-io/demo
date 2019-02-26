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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;

import io.lzz.demo.spring.batch.entity.User;

/**
 * @author q1219331697
 *
 */
@Configuration
public class AppConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	@Qualifier("step0")
	private Step step0;
	@Autowired
	@Qualifier("step1")
	private Step step1;
	@Autowired
	@Qualifier("step2")
	private Step step2;

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
		// ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		// taskExecutor.setThreadNamePrefix("spring_batch_");
		// taskExecutor.setCorePoolSize(50);
		// return taskExecutor;
		return new SimpleAsyncTaskExecutor("spring_batch_");
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")//
				// .preventRestart()//失败作业不能重启，默认true
				.start(step0)//
				.next(step1)//
				.next(step2)//
				.build();
	}
}
