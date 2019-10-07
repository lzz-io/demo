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

package io.lzz.demo.jbpm.config;

import javax.persistence.EntityManagerFactory;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * RuntimeManagerConfig
 * 
 * @author q1219331697
 */
// @Configuration
public class RuntimeManagerConfig {

	@Autowired
	private EntityManagerFactory emf;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Bean
	public RuntimeManager runtimeManager() {
		// RuntimeEnvironment runtimeEnvironment =
		// RuntimeEnvironmentBuilder.Factory.get()//
		// .newDefaultBuilder()//
		// .addAsset(ResourceFactory.newClassPathResource("bpmn/process_04_humantask.bpmn"),
		// ResourceType.BPMN2)//
		// .entityManagerFactory(emf)//
		// .get();
		DefaultRuntimeEnvironment runtimeEnvironment = new DefaultRuntimeEnvironment(emf, true);
		runtimeEnvironment.init();
		runtimeEnvironment.addToEnvironment(EnvironmentName.TRANSACTION_MANAGER, transactionManager);
		runtimeEnvironment.addAsset(ResourceFactory.newClassPathResource("bpmn/process_04_humantask.bpmn"),
				ResourceType.BPMN2);
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(runtimeEnvironment);
	}

}
