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

package io.lzz.demo.db.multi.ds.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author q1219331697
 *
 */
@Configuration
public class DataSourceConfig {

	// 定义2个name
	// @Primary
	@Bean("primaryDataSource")
	@ConfigurationProperties(prefix = "app.datasource.primary")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean("secondaryDataSource")
	@ConfigurationProperties(prefix = "app.datasource.secondary")
	public DataSource secondaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public DataSource dataSource() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("primaryDataSource", primaryDataSource());
		targetDataSources.put("secondaryDataSource", secondaryDataSource());
		// DynamicDataSourceHolder.dataSources.add("primaryDataSource");
		// DynamicDataSourceHolder.dataSources.add("secondaryDataSource");

		DynamicDataSource dataSource = new DynamicDataSource();
		// 设置数据源映射
		dataSource.setTargetDataSources(targetDataSources);
		// 设置默认数据源，当无法映射到数据源时会使用默认数据源
		dataSource.setDefaultTargetDataSource(primaryDataSource());
		// dataSource.afterPropertiesSet();

		return dataSource;
	}

}
