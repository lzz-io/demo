package io.lzz.demo.jbpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Process1TestByJbpmJUnitBaseTestCase extends JbpmJUnitBaseTestCase {

	private static final Logger log = LoggerFactory.getLogger(Process1TestByJbpmJUnitBaseTestCase.class);

	public Process1TestByJbpmJUnitBaseTestCase() {
		super(true, true);
	}

	@Test
	public void testProcess1() {
		RuntimeManager runtimeManager = createRuntimeManager("bpmn/process_1.bpmn");
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(null);

		KieSession ksession = runtimeEngine.getKieSession();
		
		ProcessInstance processInstance = ksession.startProcess("process_1");
		log.info("processInstance={}", processInstance);

		TaskService taskService = runtimeEngine.getTaskService();

		// execute task
		String actorId = "actor1";
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");
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