package io.lzz.demo.spring.batch.job.remotepartitioning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.AggregatorFactoryBean;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint;
import org.springframework.integration.redis.outbound.RedisQueueOutboundChannelAdapter;

@Slf4j
@Configuration
public class RemotePartitioningConfig {

    @Autowired
    private JobExplorer jobExplorer;

    /*
     * Configuration of the manager side
     */
    @Bean
    @ServiceActivator(inputChannel = "remotePartitioningManagerInputChannel")
    public AggregatorFactoryBean partitioningMessageHandler() {
        AggregatorFactoryBean aggregatorFactoryBean = new AggregatorFactoryBean();
        aggregatorFactoryBean.setProcessorBean(partitionHandler());
        aggregatorFactoryBean.setOutputChannel(remotePartitioningManagerOutputReplyChannel());
        // configure other propeties of the aggregatorFactoryBean
        return aggregatorFactoryBean;
    }

    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public MessageChannelPartitionHandler partitionHandler() {
        MessageChannelPartitionHandler partitionHandler = new MessageChannelPartitionHandler();
        partitionHandler.setStepName("step1");
        partitionHandler.setGridSize(8);
        partitionHandler.setReplyChannel(remotePartitioningManagerOutputReplyChannel());
        MessagingTemplate template = new MessagingTemplate();
        template.setDefaultChannel(remotePartitioningManagerOutputRequestChannel());
        template.setReceiveTimeout(100000);
        partitionHandler.setMessagingOperations(template);
        return partitionHandler;
    }

    @Bean
    public QueueChannel remotePartitioningManagerOutputReplyChannel() {
        return new QueueChannel();
    }

    @Bean
    public DirectChannel remotePartitioningManagerOutputRequestChannel() {
        return new DirectChannel();
    }

    // @Bean
    // public IntegrationFlow remotePartitioningManagerOutboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows
    //             .from(remotePartitioningManagerOutputRequestChannel())
    //             .handle(Jms.outboundAdapter(connectionFactory)
    //                     .destination("remotePartitioning.manager2worker"))
    //             .get();
    // }
    @Bean
    public IntegrationFlow remotePartitioningManagerOutboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueOutboundChannelAdapter redisQueueOutboundChannelAdapter
                = new RedisQueueOutboundChannelAdapter("remotePartitioning.manager2worker", connectionFactory);
        redisQueueOutboundChannelAdapter.setExtractPayload(false);
        return IntegrationFlow
                .from(remotePartitioningManagerOutputRequestChannel())
                .log()
                .handle(redisQueueOutboundChannelAdapter)
                .get();
    }

    // @Bean
    // public IntegrationFlow remotePartitioningManagerInboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows
    //             .from(Jms.messageDrivenChannelAdapter(connectionFactory)
    //                     .configureListenerContainer(c -> c.subscriptionDurable(false))
    //                     .destination("remotePartitioning.worker2manager"))
    //             .channel(remotePartitioningManagerInputChannel())
    //             .get();
    // }
    @Bean
    public RedisQueueMessageDrivenEndpoint remotePartitioningManagerInboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint
                = new RedisQueueMessageDrivenEndpoint("remotePartitioning.worker2manager", connectionFactory);
        redisQueueMessageDrivenEndpoint.setOutputChannel(remotePartitioningManagerInputChannel());
        redisQueueMessageDrivenEndpoint.setExpectMessage(true);
        return redisQueueMessageDrivenEndpoint;
    }

    @Bean
    public DirectChannel remotePartitioningManagerInputChannel() {
        return new DirectChannel();
    }

    // worker******************************************************************
    /*
     * Configuration of the worker side
     */
    @Bean
    @ServiceActivator(inputChannel = "remotePartitioningWorkInputChannel",
            outputChannel = "remotePartitioningWorkOutputChannel")
    public StepExecutionRequestHandler serviceActivator() {
        return stepExecutionRequestHandler();
    }

    @Bean
    public StepExecutionRequestHandler stepExecutionRequestHandler() {
        StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
        stepExecutionRequestHandler.setJobExplorer(jobExplorer);
        stepExecutionRequestHandler.setStepLocator(stepLocator());
        return stepExecutionRequestHandler;
    }

    @Bean
    public StepLocator stepLocator() {
        return new BeanFactoryStepLocator();
    }

    // @Bean
    // public IntegrationFlow remotePartitioningWorkInputChannelFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows
    //             .from(Jms.messageDrivenChannelAdapter(connectionFactory)
    //                     .configureListenerContainer(c -> c.subscriptionDurable(false))
    //                     .destination("remotePartitioning.manager2worker"))
    //             .channel(remotePartitioningWorkInputChannel())
    //             .get();
    // }
    @Bean
    public RedisQueueMessageDrivenEndpoint remotePartitioningWorkInputChannelFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint
                = new RedisQueueMessageDrivenEndpoint("remotePartitioning.manager2worker", connectionFactory);
        redisQueueMessageDrivenEndpoint.setOutputChannel(remotePartitioningWorkInputChannel());
        redisQueueMessageDrivenEndpoint.setExpectMessage(true);
        return redisQueueMessageDrivenEndpoint;
    }

    @Bean
    public DirectChannel remotePartitioningWorkInputChannel() {
        return new DirectChannel();
    }

    // @Bean
    // public IntegrationFlow remotePartitioningWorkOutputChannelFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows
    //             .from(remotePartitioningWorkOutputChannel())
    //             .handle(Jms.outboundAdapter(connectionFactory)
    //                     .destination("remotePartitioning.worker2manager"))
    //             .get();
    // }
    @Bean
    public IntegrationFlow remotePartitioningWorkOutputChannelFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueOutboundChannelAdapter redisQueueOutboundChannelAdapter
                = new RedisQueueOutboundChannelAdapter("remotePartitioning.worker2manager", connectionFactory);
        redisQueueOutboundChannelAdapter.setExtractPayload(false);
        return IntegrationFlow
                .from(remotePartitioningWorkOutputChannel())
                .log()
                .handle(redisQueueOutboundChannelAdapter)
                .get();
    }

    @Bean
    public DirectChannel remotePartitioningWorkOutputChannel() {
        return new DirectChannel();
    }

}
