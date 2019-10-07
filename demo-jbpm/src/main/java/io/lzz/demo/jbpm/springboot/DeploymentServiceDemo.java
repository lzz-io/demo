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

package io.lzz.demo.jbpm.springboot;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author q1219331697
 *
 */
@Component
public class DeploymentServiceDemo {

	@Autowired
	private DeploymentService deploymentService;

	public void deploy() {
		// create deployment unit by giving GAV
		DeploymentUnit deploymentUnit = new KModuleDeploymentUnit("groupId", "artifactId", "1.0.0");

		// deploy
		deploymentService.deploy(deploymentUnit);

		// retrieve deployed unit
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());

		// get runtime manager
		RuntimeManager runtimeManager = deployedUnit.getRuntimeManager();

		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(null);

		KieSession kieSession = runtimeEngine.getKieSession();

		kieSession.startProcess("processId");
	}
}
