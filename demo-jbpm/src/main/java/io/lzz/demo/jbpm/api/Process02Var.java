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

package io.lzz.demo.jbpm.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 * @author q1219331697
 *
 */
public class Process02Var {

	public static void main(String[] args) {
		KieHelper kieHelper = new KieHelper();
		KieBase kieBase = kieHelper.addResource(ResourceFactory.newClassPathResource("bpmn/process_02_var.bpmn"))
				.build();
		KieSession kieSession = kieBase.newKieSession();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("varString", "reset varString");
		parameters.put("varInt", 20);
		parameters.put("varDate", new Date());
		ProcessInstance processInstance = kieSession.startProcess("process_02_var", parameters);

		WorkflowProcessInstance workflowProcessInstance = (WorkflowProcessInstance) processInstance;
		System.out.println(workflowProcessInstance.getVariable("varString"));
		System.out.println(workflowProcessInstance.getVariable("varInt"));
		System.out.println(workflowProcessInstance.getVariable("varDate"));
	}

}
