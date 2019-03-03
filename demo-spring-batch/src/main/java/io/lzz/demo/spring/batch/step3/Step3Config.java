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

package io.lzz.demo.spring.batch.step3;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import io.lzz.demo.spring.batch.entity.User;

/**
 * @author q1219331697
 *
 */
@Configuration
public class Step3Config {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Step3ExecutionListener step3ExecutionListener;
	@Autowired
	private Step3ItemReadListener step3ItemReadListener;
	@Autowired
	private Step3ItemWriteListener step3ItemWriteListener;

	@Autowired
	@Qualifier("step3QueryProvider")
	private PagingQueryProvider step3QueryProvider;

	// ItemReader
	@Bean
	public ItemReader<User> step3ItemReader() {
		JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
		reader.setName("step3ItemReader");
		reader.setDataSource(dataSource);
		reader.setQueryProvider(step3QueryProvider);
		reader.setRowMapper(new BeanPropertyRowMapper<>(User.class));
		reader.setPageSize(5);
		return reader;
	}

	@Bean
	public SqlPagingQueryProviderFactoryBean step3QueryProvider() {
		SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();

		provider.setDataSource(dataSource);
		provider.setSelectClause("select id, username, createTime");
		provider.setFromClause("from tb_user");
		Map<String, Order> sortKeys = new LinkedHashMap<>();
		sortKeys.put("id", Order.ASCENDING);
		provider.setSortKeys(sortKeys);

		return provider;
	}

	// Processor
	@Bean
	public ItemProcessor<User, User> step3ItemProcessor() {
		return new Step3ItemProcessor();
	}

	// ItemWriter
	@Bean
	public ItemWriter<User> step3ItemWriter() {
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
		writer.setName("step3ItemWriter");

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

	@Bean
	public Step step3(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager transactionManager,
			TaskExecutor taskExecutor) {
		return stepBuilderFactory.get("step3")//
				// .transactionManager(transactionManager)//
				.<User, User>chunk(3)//
				.reader(step3ItemReader())//
				.processor(step3ItemProcessor())//
				.writer(step3ItemWriter())//
				.faultTolerant()// 失败处理
				.retryLimit(3)// 重试次数
				.retry(Exception.class)// 重试异常必须配置
				.skipLimit(Integer.MAX_VALUE)//
				.skip(Exception.class)// 跳过异常，通常用自定义异常
				.noSkip(FileNotFoundException.class)// 哪些异常不跳过
				.listener(step3ItemReadListener)//
				// .listener(step3ItemProcessListener)//
				.listener(step3ItemWriteListener)//
				// .listener(step3ChunkListener)//
				.listener(step3ExecutionListener)//
				// .listener(step3SkipListener)//
				// .taskExecutor(taskExecutor)// 多线程步骤
				// .throttleLimit(8)// 最大使用线程池数目
				// .allowStartIfComplete(true)//
				.build();
	}

}
