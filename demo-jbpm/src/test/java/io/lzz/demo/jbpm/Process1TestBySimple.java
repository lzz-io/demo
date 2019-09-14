package io.lzz.demo.jbpm;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.test.util.db.DataSourceFactory;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class Process1TestBySimple {

    private static final Logger log = LoggerFactory.getLogger(Process1TestBySimple.class);

    private static RuntimeManager getRuntimeManager(String process) {
        Properties driverProperties = new Properties();
        driverProperties.put("user", "sa");
        driverProperties.put("password", "sa");
        driverProperties.put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
        driverProperties.put("driverClassName", "org.h2.Driver");
        driverProperties.put("className", "org.h2.jdbcx.JdbcDataSource");
        PoolingDataSourceWrapper pdsw = DataSourceFactory.setupPoolingDataSource("jdbc/jbpm-ds", driverProperties);

        // create the entity manager factory
        EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        // TransactionManager tm = TransactionManagerServices.getTransactionManager();

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(new UserGroupCallback() {
            public List<String> getGroupsForUser(String userId) {
                List<String> result = new ArrayList<String>();
                // if ("sales-rep".equals(userId)) {
                //     result.add("sales");
                // } else if ("john".equals(userId)) {
                //     result.add("PM");
                // }
                return result;
            }

            public boolean existsUser(String arg0) {
                return true;// 任何用户都认为存在
            }

            public boolean existsGroup(String arg0) {
                return true;// 任何用户组都认为存在
            }
        }).addAsset(KieServices.Factory.get().getResources().newClassPathResource(process), ResourceType.BPMN2).get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }

    @Test
    public void testProcess1() throws Exception {

        KieHelper kieHelper = new KieHelper();
        KieBase kieBase = kieHelper
                .addResource(ResourceFactory.newClassPathResource("bpmn/process_1.bpmn"))
                .build();

        RuntimeManager manager = getRuntimeManager("bpmn/process_1.bpmn");
        RuntimeEngine runtime = manager.getRuntimeEngine(null);
        KieSession kieSession = runtime.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", "krisv");
        params.put("description", "Need a new laptop computer");
        // kieSession.startProcess("process_1", params);
        ProcessInstance processInstance = kieSession.startProcess("process_1", params);
        log.info("processInstance={}", processInstance);

        TaskService taskService = runtime.getTaskService();
        // TaskService taskService = null;

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
    }
}