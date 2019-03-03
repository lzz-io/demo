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

package io.lzz.demo.spring.batch.step1;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import io.lzz.demo.spring.batch.entity.User;

/**
 * @author q1219331697
 *
 */
@Configuration
public class Step1Config {

	private static final Logger log = LoggerFactory.getLogger(Step1Config.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Step1ItemWriteListener step1ItemWriteListener;

	@Autowired
	private Step1ExecutionListener step1ExecutionListener;

	@SuppressWarnings("unused")
	@Autowired
	private TaskExecutor taskExecutor;

	// ItemReader
	// @Bean
	public ItemReader<User> step1ItemReader() {
		List<User> list = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			User user = new User();
			user.setUsername("username" + i);
			user.setCreateTime(new Date());
			list.add(user);
			log.debug("{}", user);
		}
		log.info("list.size={}", list.size());
		// ListItemReader非线程安全，勿在多线程下使用
		return new ListItemReader<>(list);
	}

	// Processor not need

	// ItemWriter
	@Bean
	public ItemWriter<User> step1ItemWriter() {
		JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
		String sql = "insert into tb_user (id, username, createTime) " + "values (:id, :username, :createTime)";
		writer.setSql(sql);
		return writer;
	}

	@Bean
	public Step step1(PlatformTransactionManager transactionManager) {
		return stepBuilderFactory.get("step1")//
				// .transactionManager(transactionManager)//
				.<User, User>chunk(3)//
				.reader(step1ItemReader())//
				// .processor(processor())//
				.writer(step1ItemWriter())//
				.faultTolerant()// 失败处理
				.retryLimit(3)// 重试次数
				.retry(Exception.class)// 重试异常必须配置
				.skipLimit(Integer.MAX_VALUE)//
				.skip(Exception.class)// 跳过异常，通常用自定义异常
				.noSkip(FileNotFoundException.class)// 哪些异常不跳过
				// .listener(step1ItemReadListener)//
				// .listener(step1ItemProcessListener)//
				.listener(step1ItemWriteListener)//
				// .listener(step1ChunkListener)//
				.listener(step1ExecutionListener)//
				// .listener(step1SkipListener)//
				// .taskExecutor(taskExecutor)// 多线程步骤
				// .throttleLimit(1)// 最大使用线程池数目
				// .allowStartIfComplete(true)//
				.build();
	}

}
