package io.lzz.demo.jbpm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Process1TestBySpringBoot {

    private static final Logger log = LoggerFactory.getLogger(Process1TestBySpringBoot.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private RuntimeManagerFactory runtimeManagerFactory;
    // @Autowired
    // private RuntimeDataService runtimeDataService;

    // @Autowired
    // private RuntimeManager runtimeManager;
    // @Autowired
    // private TaskService taskService;

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
        // log.info("{}", runtimeManagerFactory.);
        // // first configure environment that will be used by RuntimeManager
        // RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()//
        // .newDefaultInMemoryBuilder()//
        // .addAsset(ResourceFactory.newClassPathResource("bpmn/process_1.bpmn"), //
        // ResourceType.BPMN2)
        // .get();
        //
        // // next create RuntimeManager - in this case singleton strategy is chosen
        // RuntimeManager manager = RuntimeManagerFactory.Factory.get()//
        // .newSingletonRuntimeManager(environment);
        //
        // // then get RuntimeEngine out of manager - using empty context as singleton
        // does
        // // not keep track
        // // of runtime engine as there is only one
        // RuntimeEngine runtimeEngine =
        // runtimeManager.getRuntimeEngine(EmptyContext.get());
        // RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(null);
        //
        // KieSession ksession = runtimeEngine.getKieSession();
        // // KieSession ksession = null;
        //
        // ProcessInstance processInstance = ksession.startProcess("process_1");
        // log.info("processInstance={}", processInstance);
        //
        // TaskService taskService = runtimeEngine.getTaskService();
        // TaskService taskService = null;

        // execute task
        // String actorId = "actor1";
        // List<TaskSummary> list =
        // taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");
        // TaskSummary task = list.get(0);
        // taskService.start(task.getId(), actorId);
        // Map<String, Object> results = new HashMap<String, Object>();
        // // add results here
        // taskService.complete(task.getId(), actorId, results);
        // log.info("processInstance={}", processInstance);
        //
        // // execute task
        // actorId = "actor2";
        // // list = taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");
        // list = taskService.getTasksAssignedAsPotentialOwner(actorId, "");
        // task = list.get(0);
        // taskService.start(task.getId(), actorId);
        // results = new HashMap<String, Object>();
        // // add results here
        // taskService.complete(task.getId(), actorId, results);
        // log.info("processInstance={}", processInstance);

        // do your checks here
        // for example, assertProcessInstanceCompleted(processInstance.getId(),
        // ksession);

        // manager.disposeRuntimeEngine(runtimeEngine);
        // manager.close();
    }

}