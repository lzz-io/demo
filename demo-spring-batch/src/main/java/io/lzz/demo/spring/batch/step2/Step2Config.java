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

package io.lzz.demo.spring.batch.step2;

import java.io.FileNotFoundException;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.lzz.demo.spring.batch.entity.User;

/**
 * @author q1219331697
 *
 */
@Configuration
public class Step2Config {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Step2ExecutionListener step2ExecutionListener;
	@Autowired
	private Step2ItemReadListener step2ItemReadListener;
	@Autowired
	private Step2ItemWriteListener step2ItemWriteListener;

	// ItemReader
	@Bean
	public ItemReader<User> step2ItemReader() {
		JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		String sql = "SELECT t.* FROM TB_USER t ORDER BY ID ASC";
		reader.setSql(sql);
		RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
		reader.setRowMapper(rowMapper);
		return reader;
	}

	// Processor
	@Bean
	public ItemProcessor<User, User> step2ItemProcessor() {
		return new Step2ItemProcessor();
	}

	// ItemWriter
	@Bean
	public ItemWriter<User> step2ItemWriter() {
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
		writer.setName("step2ItemWriter");

		Resource resource = new FileSystemResource("tmp/step2.csv");
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

	// @Autowired
	// @Qualifier("step2ItemWriter")
	// private ItemWriter<User> writer;

	@Bean
	public Step step2(StepBuilderFactory stepBuilderFactory, TaskExecutor taskExecutor) {
		return stepBuilderFactory.get("step2")//
				.<User, User>chunk(3)//
				.reader(step2ItemReader())//
				.processor(step2ItemProcessor())//
				.writer(step2ItemWriter())//
				.faultTolerant()// 失败处理
				.retryLimit(3)// 重试次数
				.retry(Exception.class)// 重试异常必须配置
				.skipLimit(Integer.MAX_VALUE)//
				.skip(Exception.class)// 跳过异常，通常用自定义异常
				.noSkip(FileNotFoundException.class)// 哪些异常不跳过
				.listener(step2ItemReadListener)//
				// .listener(step2ItemProcessListener)//
				.listener(step2ItemWriteListener)//
				// .listener(step2ChunkListener)//
				.listener(step2ExecutionListener)//
				// .listener(step2SkipListener)//
				// .taskExecutor(taskExecutor)// 多线程步骤
				// .throttleLimit(4)// 最大使用线程池数目
				.build();
	}

}
