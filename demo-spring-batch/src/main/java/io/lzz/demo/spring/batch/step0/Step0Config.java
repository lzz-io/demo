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

package io.lzz.demo.spring.batch.step0;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author q1219331697
 *
 */
@Configuration
public class Step0Config {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private Step0Tasklet step0Tasklet;

	@Autowired
	private Step0ExecutionListener step0ExecutionListener;

	@Bean
	public Step step0() {
		return stepBuilderFactory.get("step0")//
				.tasklet(step0Tasklet)//
				.listener(step0ExecutionListener)//
				// .taskExecutor(taskExecutor)// 多线程
				// .throttleLimit(4)// 最大使用线程池数目
				.build();
	}

}
