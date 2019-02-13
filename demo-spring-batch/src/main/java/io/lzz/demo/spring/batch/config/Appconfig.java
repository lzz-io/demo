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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.task.UserItemProcessor;

/**
 * @author q1219331697
 *
 */
@Configuration
@EnableBatchProcessing
public class Appconfig {

	private static final Logger log = LoggerFactory.getLogger(Appconfig.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

//	@Bean
//	public FlatFileItemReader<User> reader() {
//		BeanWrapperFieldSetMapper<User> mapper = new BeanWrapperFieldSetMapper<>();
//		mapper.setTargetType(User.class);
//		return new FlatFileItemReaderBuilder<User>() //
//				.name("reader") //
//				.encoding("UTF-8") //
//				.resource(new ClassPathResource("data.csv")) //
//				.delimited() //
//				// .quoteCharacter(',')//
//				.delimiter(",")//
//				.names(new String[] { "id", "username", "createTime" }) //
//				.fieldSetMapper(mapper) //
//				.build();
//	}
	
	@Bean
	public FlatFileItemReader<User> reader() {
		// TODO 去除首行
		FlatFileItemReader<User> reader = new FlatFileItemReader<>();
		reader.setName("reader");
		reader.setResource(new ClassPathResource("data.csv"));
		reader.setEncoding("UTF-8");
		reader.setLineMapper(new DefaultLineMapper<User>() {{
            setLineTokenizer(new DelimitedLineTokenizer(",") {{
                setNames(new String[]{"id", "username", "createTime"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                setTargetType(User.class);
            }});
        }});
		
		return reader;
	}

	@Bean
	public UserItemProcessor processor() {
		return new UserItemProcessor();
	}

	@Bean
	public FlatFileItemWriter<User> writer(User user) {
		LineAggregator<User> lineAggregator = new LineAggregator<User>() {
			@Override
			public String aggregate(User item) {
				log.debug("{}", item.toString());
				return item.toString();
			}
		};

		return new FlatFileItemWriterBuilder<User>() //
				.append(true) //
				.name("writer") //
				.encoding("UTF-8") //
				.resource(new ClassPathResource("out.csv")) //
				.lineAggregator(lineAggregator).build();
	}

	@Bean
	public Step step1(FlatFileItemWriter<User> writer) {
		return stepBuilderFactory.get("step1")//
				.<User, User>chunk(3)//
				.reader(reader())//
				.processor(processor())//
				.writer(writer)//
				.build();
	}

	@Bean
	public Job job(Step step1) {
		return jobBuilderFactory.get("job")//
				.flow(step1).end().build();
	}
}
