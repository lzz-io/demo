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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(Step0Config.class);

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private Step0Tasklet step0Tasklet;

	@Bean
	public Step step0() {
		return stepBuilderFactory.get("step0")//
				.tasklet(step0Tasklet)//
				// .taskExecutor(taskExecutor)// 多线程步骤
				// .throttleLimit(4)// 最大使用线程池数目
				.build();
	}

}
