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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * @author q1219331697
 *
 */
// @Component
public class CustomBatchDataSourceInitializer extends BatchDataSourceInitializer {

	/**
	 * @param dataSource
	 * @param resourceLoader
	 * @param properties
	 */
	public CustomBatchDataSourceInitializer(@Qualifier("batchDataSource") DataSource dataSource,
			ResourceLoader resourceLoader, BatchProperties properties) {
		super(dataSource, resourceLoader, properties);
	}

}
