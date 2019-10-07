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

package io.lzz.demo.jbpm;

import java.io.File;

import org.appformer.maven.integration.MavenRepository;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author q1219331697
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JbpmApplicationTest {

	private static final Logger log = LoggerFactory.getLogger(JbpmApplicationTest.class);

	@Autowired
	private ApplicationContext applicationContext;

	// @Autowired
	// private RuntimeManager runtimeManager;
	
	static final String ARTIFACT_ID = "evaluation";
    static final String GROUP_ID = "org.jbpm.test";
    static final String VERSION = "1.0.0";
    
	@Test
	public final void testMain() {
		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		log.info("beanDefinitionNames.length:{}", beanDefinitionNames.length);

		for (String beanName : beanDefinitionNames) {
			log.info("beanName:{}", beanName);
		}

		// log.info(runtimeManager.toString());

		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
		File kjar = new File("../kjars/evaluation/jbpm-module.jar");
		File pom = new File("../kjars/evaluation/pom.xml");
		MavenRepository repository = KieMavenRepository.getKieMavenRepository();
		repository.installArtifact(releaseId, kjar, pom);
		
		EntityManagerFactoryManager.get().clear();
	}

}
