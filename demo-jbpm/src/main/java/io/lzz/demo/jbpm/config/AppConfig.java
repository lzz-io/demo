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

import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author q1219331697
 */
@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    // private static String location = "classpath:/roles.properties";

    @Autowired
    private org.springframework.transaction.jta.JtaTransactionManager transactionManager;
    @Autowired
    private org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Autowired
    private RuntimeManagerFactory runtimeManagerFactory;

    // @Bean
    // public UserGroupCallback userGroupCallback() {
    //     return new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(location);
    // }

    // @Bean
    // public DefinitionService definitionService() {
    //     return new org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl();
    // }

    // @Bean
    // public RuntimeManagerFactory runtimeManagerFactory() {
    //     SpringRuntimeManagerFactoryImpl rmf = new SpringRuntimeManagerFactoryImpl();
    //     rmf.setUserGroupCallback(this.userGroupCallback());
    //     rmf.setTransactionManager(transactionManager);
    //     return rmf;
    // }

    // // first configure environment that will be used by RuntimeManager
    // @Bean
    // public RuntimeEnvironment runtimeEnvironment() {
    //     // EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
    //     log.info("{}", entityManagerFactory);
    //     return RuntimeEnvironmentBuilder.Factory.get()//
    //             // .newDefaultInMemoryBuilder()//
    //             .newDefaultBuilder()//
    //             // .addAsset(ResourceFactory.newClassPathResource("bpmn/process_1.bpmn"), ResourceType.BPMN2)//
    //             .addAsset(KieServices.Factory.get().getResources().newClassPathResource("bpmn/process_1.bpmn"), ResourceType.BPMN2)//
    //             .get();
    // }

    // // next create RuntimeManager - in this case singleton strategy is chosen
    // @Bean
    // public RuntimeManager runtimeManager() {
    //     return RuntimeManagerFactory.Factory.get()//
    //             .newSingletonRuntimeManager(runtimeEnvironment());
    // }

    // @Bean
    // public IdentityProvider identityProvider() {
    //     // System.err.println("identityProvider");
    //     log.debug("identityProvider");
    //     // return new org.jbpm.springboot.SpringSecurityIdentityProvider();
    //     return new org.jbpm.springboot.security.SpringSecurityIdentityProvider();
    // }
    //
    // @Bean
    // public TransactionalCommandService transactionCmdService() {
    //     System.err.println("TransactionalCommandService");
    //     TransactionalCommandService tc = new TransactionalCommandService(entityManagerFactory.getObject());
    //     return tc;
    // }
    //
    // @Bean
    // public RuntimeDataService runtimeDataService() {
    //     RuntimeDataServiceImpl rd = new RuntimeDataServiceImpl();
    //     rd.setCommandService(transactionCmdService());
    //     rd.setIdentityProvider(identityProvider());
    //     return rd;
    // }
    //
    // @Bean
    // public DeploymentService deploymentService() {
    //     KModuleDeploymentService kd = new KModuleDeploymentService();
    //     kd.setEmf(entityManagerFactory.getObject());
    //     kd.setBpmn2Service(this.definitionService());
    //     kd.setManagerFactory(this.runtimeManagerFactory());
    //     kd.setIdentityProvider(this.identityProvider());
    //     kd.setRuntimeDataService(this.runtimeDataService());
    //     kd.setFormManagerService(new FormManagerServiceImpl());
    //     kd.onInit();
    //     return kd;
    // }
    //
    // @Bean
    // public FormManagerService formService() {
    //     FormManagerService fm = new FormManagerServiceImpl();
    //     return fm;
    // }
    //
    // @Bean
    // public ProcessService processService() {
    //     ProcessServiceImpl ps = new ProcessServiceImpl();
    //     ps.setDataService(this.runtimeDataService());
    //     ps.setDeploymentService(this.deploymentService());
    //     return ps;
    // }
    //
    // @Bean
    // public UserTaskService userTaskService() {
    //     UserTaskServiceImpl ut = new UserTaskServiceImpl();
    //     ut.setDataService(this.runtimeDataService());
    //     ut.setDeploymentService(this.deploymentService());
    //     return ut;
    // }
    //
    // @Bean
    // public DeploymentStore deploymentStore() {
    //     DeploymentStore ds = new DeploymentStore();
    //     ds.setCommandService(this.transactionCmdService());
    //     return ds;
    // }
    //
    // @Bean
    // public QueryService queryService() {
    //     QueryServiceImpl qs = new QueryServiceImpl();
    //     qs.setCommandService(this.transactionCmdService());
    //     qs.setIdentityProvider(this.identityProvider());
    //     return qs;
    // }
}
