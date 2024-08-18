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

/**
 * @author q1219331697
 *
 */
// @Configuration
public class DataSourceConfig {

	// // 定义2个name
	// @Primary
	// @Bean({ "dataSource", "bizDataSource" })
	// @ConfigurationProperties(prefix = "app.datasource.biz")
	// public DataSource dataSource() {
	// 	return DataSourceBuilder.create().type(HikariDataSource.class).build();
	// }
	//
	// // @Primary
	// @Bean("batchDataSource")
	// @ConfigurationProperties(prefix = "app.datasource.batch")
	// public DataSource batchDataSource() {
	// 	return DataSourceBuilder.create().type(HikariDataSource.class).build();
	// }

	// @Bean
	// public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
	// 	JpaTransactionManager txManager = new JpaTransactionManager();
	// 	txManager.setEntityManagerFactory(entityManagerFactory);
	// 	return txManager;
	// }

}
