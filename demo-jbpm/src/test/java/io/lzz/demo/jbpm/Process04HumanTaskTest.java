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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author q1219331697
 *
 */
public class Process04HumanTaskTest extends JbpmJUnitBaseTestCase {

	private static final Logger log = LoggerFactory.getLogger(Process04HumanTaskTest.class);

	public Process04HumanTaskTest() {
		super(true, true);
		init();
	}

	public void init() {
		this.userGroupCallback = new UserGroupCallback() {
			@Override
			public List<String> getGroupsForUser(String userId) {
				List<String> list = new ArrayList<>();
				return list;
			}

			@Override
			public boolean existsUser(String userId) {
				return true;
			}

			@Override
			public boolean existsGroup(String groupId) {
				return true;
			}
		};

		// 配置数据源
		setPersistenceProperty("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
		setPersistenceProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/jbpm");
		setPersistenceProperty("javax.persistence.jdbc.user", "root");
		setPersistenceProperty("javax.persistence.jdbc.password", "123456");
		// setPersistenceProperty("javax.persistence.transactionType", "JTA");
		// setPersistenceProperty("hibernate.connection.driver_class",
		// "com.mysql.cj.jdbc.Driver");
		// setPersistenceProperty("hibernate.connection.url",
		// "jdbc:mysql://localhost:3306/jbpm");
		// setPersistenceProperty("hibernate.connection.username", "root");
		// setPersistenceProperty("hibernate.connection.password", "123456");
		// hibernate实现
		setPersistenceProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		setPersistenceProperty("hibernate.show_sql", "true");
		// setPersistenceProperty("hibernate.format_sql", "true");
		setPersistenceProperty("hibernate.hbm2ddl.auto", "update");

		// setPersistenceProperty("hibernate.transaction.manager_lookup_class",
		// "com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple");
		// setPersistenceProperty("hibernate.current_session_context_class", "jta");

		setPersistenceProperty("", "");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Process04HumanTaskTest.tearDown()");
	}

	@Test
	public void testProcess04HumanTask() {
		RuntimeManager runtimeManager = createRuntimeManager("bpmn/process_04_humantask.bpmn");
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(null);
		KieSession kieSession = runtimeEngine.getKieSession();
		ProcessInstance processInstance = kieSession.startProcess("process_04_humantask");
		log.info("processInstance={}", processInstance);

		TaskService taskService = runtimeEngine.getTaskService();

		// execute task
		String actorId = "actor1";
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(actorId, "");
		TaskSummary task = list.get(0);
		taskService.start(task.getId(), actorId);
		Map<String, Object> results = new HashMap<String, Object>();
		// add results here
		taskService.complete(task.getId(), actorId, results);
		log.info("processInstance={}", processInstance);

		// execute task
		actorId = "actor2";
		// list = taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");
		list = taskService.getTasksAssignedAsPotentialOwner(actorId, "");
		task = list.get(0);
		taskService.start(task.getId(), actorId);
		results = new HashMap<String, Object>();
		// add results here
		taskService.complete(task.getId(), actorId, results);
		log.info("processInstance={}", processInstance);

		// do your checks here
		// for example, assertProcessInstanceCompleted(processInstance.getId(),
		// ksession);

		manager.disposeRuntimeEngine(runtimeEngine);
		manager.close();
	}
}
