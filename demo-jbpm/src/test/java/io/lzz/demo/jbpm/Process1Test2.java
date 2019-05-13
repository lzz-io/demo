package io.lzz.demo.jbpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Process1Test2 {

	private static final Logger log = LoggerFactory.getLogger(Process1Test2.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private RuntimeManagerFactory runtimeManagerFactory;

	@Test
	public void testAppContext() {
		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanDefinitionNames) {
			log.info("{} = {}", beanName, applicationContext.getBean(beanName));
		}
		log.info("size = {}", beanDefinitionNames.length);
	}

	@Test
	public void testProcess1() {

		// first configure environment that will be used by RuntimeManager
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultInMemoryBuilder()
				.addAsset(ResourceFactory.newClassPathResource("bpmn/process_1.bpmn"), ResourceType.BPMN2).get();

		// next create RuntimeManager - in this case singleton strategy is chosen
		RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);

		// then get RuntimeEngine out of manager - using empty context as singleton does
		// not keep track
		// of runtime engine as there is only one
		RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());

		KieSession ksession = runtime.getKieSession();
		// KieSession ksession = null;

		ProcessInstance processInstance = ksession.startProcess("process_1");
		log.info("processInstance={}", processInstance);

		// TaskService taskService = runtimeEngine.getTaskService();
		TaskService taskService = null;

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

		// manager.disposeRuntimeEngine(runtimeEngine);
		// manager.close();
	}

}