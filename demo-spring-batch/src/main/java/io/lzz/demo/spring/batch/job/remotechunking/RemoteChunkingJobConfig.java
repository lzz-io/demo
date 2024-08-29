package io.lzz.demo.spring.batch.job.remotechunking;

import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint;
import org.springframework.integration.redis.outbound.RedisQueueOutboundChannelAdapter;

import javax.persistence.EntityManagerFactory;
import java.util.Date;

@Configuration
public class RemoteChunkingJobConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RemoteChunkingWorkerBuilder<User, User> workerBuilder;

    @Bean
    public Job remoteChunkingJob() {
        return jobBuilderFactory.get("remoteChunkingJob")
                .start(initStep())
                .next(remoteChunkingManagerStep())
                .build();
    }

    private Step initStep() {
        return stepBuilderFactory.get("initStep")
                .tasklet((contribution, chunkContext) -> {
                    userRepository.deleteAllInBatch();
                    for (int i = 1; i <= 10; i++) {
                        userRepository.save(new User("userName" + i, new Date()));
                    }
                    return null;
                })
                .build();
    }

    @Bean
    public TaskletStep remoteChunkingManagerStep() {
        return this.managerStepBuilderFactory.get("remoteChunkingManagerStep")
                .chunk(3)
                .reader(itemReader())
                .outputChannel(remoteChunkingManagerOutputChannel()) // requests sent to workers
                .inputChannel(remoteChunkingManagerInputChannel())   // replies received from workers
                .build();
    }

    private JpaCursorItemReader<User> itemReader() {
        return new JpaCursorItemReaderBuilder<User>()
                .name("itemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select u from User u")
                .build();
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public DirectChannel remoteChunkingManagerOutputChannel() {
        return new DirectChannel();
    }

    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public QueueChannel remoteChunkingManagerInputChannel() {
        return new QueueChannel();
    }

    // @Bean
    // public IntegrationFlow remoteChunkingManagerOutboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows.from(remoteChunkingManagerOutputChannel())//
    //             .handle(Jms.outboundAdapter(connectionFactory)//
    //                     .destination("master2worker"))
    //             .get();
    // }
    @Bean
    public IntegrationFlow remoteChunkingManagerOutboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueOutboundChannelAdapter redisQueueOutboundChannelAdapter
                = new RedisQueueOutboundChannelAdapter("master2worker", connectionFactory);
        return IntegrationFlows
                .from(remoteChunkingManagerOutputChannel())//
                .handle(redisQueueOutboundChannelAdapter)
                .get();
    }

    // @Bean
    // public IntegrationFlow remoteChunkingManagerInboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows//
    //             .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
    //                     .destination("worker2master"))
    //             .channel(remoteChunkingManagerInputChannel())//
    //             .get();
    // }
    @Bean
    public RedisQueueMessageDrivenEndpoint remoteChunkingManagerInboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint
                = new RedisQueueMessageDrivenEndpoint("worker2master", connectionFactory);
        redisQueueMessageDrivenEndpoint.setOutputChannel(remoteChunkingManagerInputChannel());
        return redisQueueMessageDrivenEndpoint;
    }

    // worker******************************************************************
    @Bean
    public IntegrationFlow remoteChunkingWorkerFlow() {
        return this.workerBuilder
                .itemProcessor(itemProcessor())
                .itemWriter(remoteChunkingWorkerItemWriter())
                .inputChannel(remoteChunkingWorkInputChannel()) // requests received from the manager
                .outputChannel(remoteChunkingWorkOutputChannel()) // replies sent to the manager
                .build();
    }

    @Bean
    public ItemProcessor<User, User> itemProcessor() {
        return item -> {
            item.setUserName(item.getUserName() + "修改");
            return item;
        };
    }

    @Bean
    public ItemWriter<User> remoteChunkingWorkerItemWriter() {
        return items -> userRepository.saveAll(items);
    }

    /*
     * Configure inbound flow (requests coming from the manager)
     */
    @Bean
    public QueueChannel remoteChunkingWorkInputChannel() {
        return new QueueChannel();
    }

    /*
     * Configure outbound flow (replies going to the manager)
     */
    @Bean
    public DirectChannel remoteChunkingWorkOutputChannel() {
        return new DirectChannel();
    }

    // @Bean
    // public IntegrationFlow remoteChunkingWorkInboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows//
    //             .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
    //                     .destination("master2worker"))
    //             .channel(remoteChunkingWorkInputChannel())//
    //             .get();
    // }
    @Bean
    public RedisQueueMessageDrivenEndpoint remoteChunkingWorkInboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint
                = new RedisQueueMessageDrivenEndpoint("master2worker", connectionFactory);
        redisQueueMessageDrivenEndpoint.setOutputChannel(remoteChunkingWorkInputChannel());
        return redisQueueMessageDrivenEndpoint;
    }

    // @Bean
    // public IntegrationFlow remoteChunkingWorkOutboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows.from(remoteChunkingWorkOutputChannel())//
    //             .handle(Jms.outboundAdapter(connectionFactory)//
    //                     .destination("worker2master"))
    //             .get();
    // }
    @Bean
    public IntegrationFlow remoteChunkingWorkOutboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueOutboundChannelAdapter redisQueueOutboundChannelAdapter
                = new RedisQueueOutboundChannelAdapter("worker2master", connectionFactory);
        return IntegrationFlows
                .from(remoteChunkingWorkOutputChannel())//
                .handle(redisQueueOutboundChannelAdapter)
                .get();
    }

}
