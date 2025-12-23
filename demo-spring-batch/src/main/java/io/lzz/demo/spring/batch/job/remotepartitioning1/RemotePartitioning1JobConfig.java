package io.lzz.demo.spring.batch.job.remotepartitioning1;

import io.lzz.demo.spring.batch.component.BasicPartitioner;
import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint;
import org.springframework.integration.redis.outbound.RedisQueueOutboundChannelAdapter;

import javax.persistence.EntityManagerFactory;
import java.util.Date;

@Slf4j
@Configuration
public class RemotePartitioning1JobConfig {

    public static final int MOD_SIZE = 8;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;

    @Bean
    public Job remotePartitioning1Job() {
        return jobBuilderFactory.get("remotePartitioning1Job")
                .start(initStep())
                .next(remotePartitioning1ManagerStep())
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
    public Step remotePartitioning1ManagerStep() {
        return this.managerStepBuilderFactory.get("remotePartitioning1ManagerStep")
                // remotePartitioningWorkerStep，worker的step名字，一定要注意！！！
                .partitioner("remotePartitioning1WorkerStep", new BasicPartitioner().setKeyName("mod"))
                .gridSize(MOD_SIZE)
                .outputChannel(remotePartitioning1ManagerOutputChannel()) // requests sent to workers
                .inputChannel(remotePartitioning1ManagerInputChannel())   // replies received from workers
                .build();
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public DirectChannel remotePartitioning1ManagerOutputChannel() {
        return new DirectChannel();
    }

    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public DirectChannel remotePartitioning1ManagerInputChannel() {
        return new DirectChannel();
    }

    // @Bean
    // public IntegrationFlow remotePartitioning1ManagerOutboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows
    //             .from(remotePartitioning1ManagerOutputChannel())
    //             .log()
    //             .handle(Jms.outboundAdapter(connectionFactory)//
    //                     .destination("remotePartitioning1.master2worker"))
    //             .get();
    // }
    @Bean
    public IntegrationFlow remotePartitioning1ManagerOutboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueOutboundChannelAdapter redisQueueOutboundChannelAdapter
                = new RedisQueueOutboundChannelAdapter("remotePartitioning1.master2worker", connectionFactory);
        redisQueueOutboundChannelAdapter.setExtractPayload(false);
        return IntegrationFlows
                .from(remotePartitioning1ManagerOutputChannel())
                .log()
                .handle(redisQueueOutboundChannelAdapter)
                .get();
    }

    // @Bean
    // public IntegrationFlow remotePartitioning1ManagerInboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows//
    //             .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
    //                     .destination("remotePartitioning1.worker2master"))
    //             .channel(remotePartitioning1ManagerInputChannel())//
    //             .log()
    //             .get();
    // }
    @Bean
    public RedisQueueMessageDrivenEndpoint remotePartitioning1ManagerInboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint
                = new RedisQueueMessageDrivenEndpoint("remotePartitioning1.worker2master", connectionFactory);
        redisQueueMessageDrivenEndpoint.setOutputChannel(remotePartitioning1ManagerInputChannel());
        redisQueueMessageDrivenEndpoint.setExpectMessage(true);
        return redisQueueMessageDrivenEndpoint;
    }

    // worker******************************************************************
    @Bean
    public Step remotePartitioning1WorkerStep() {
        return this.workerStepBuilderFactory.get("remotePartitioning1WorkerStep")
                .inputChannel(remotePartitioning1WorkInputChannel()) // requests received from the manager
                .outputChannel(remotePartitioning1WorkOutputChannel()) // replies sent to the manager
                .<User, User>chunk(4)
                .reader(remotePartitioning1WorkerStepItemReader(null))
                .processor(remotePartitioning1WorkerStepItemProcessor())
                .writer(remotePartitioning1WorkerStepItemWriter())
                .build();
    }

    /*
     * Configure inbound flow (requests coming from the manager)
     */
    @Bean
    public DirectChannel remotePartitioning1WorkInputChannel() {
        return new DirectChannel();
    }

    /*
     * Configure outbound flow (replies going to the manager)
     */
    @Bean
    public DirectChannel remotePartitioning1WorkOutputChannel() {
        return new DirectChannel();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<User> remotePartitioning1WorkerStepItemReader(
            @Value("#{stepExecutionContext['mod']}") Integer mod) {
        return new JpaPagingItemReaderBuilder<User>()
                .name("remotePartitioning1WorkerStepItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select u from User u where mod(id, " + MOD_SIZE + ") = " + mod)
                // .queryString("select u from User u")
                .pageSize(3)
                .build();
    }

    @Bean
    public ItemProcessor<User, User> remotePartitioning1WorkerStepItemProcessor() {
        return item -> {
            item.setUserName(item.getUserName() + "修改");
            return item;
        };
    }

    @Bean
    public ItemWriter<User> remotePartitioning1WorkerStepItemWriter() {
        return items -> {
            userRepository.saveAll(items);
            log.info("remotePartitioning1WorkerItemWriter: {}", items);
        };
    }

    // @Bean
    // public IntegrationFlow remotePartitioning1WorkInboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows//
    //             .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
    //                     .destination("remotePartitioning1.master2worker"))
    //             .channel(remotePartitioning1WorkInputChannel())//
    //             .log()
    //             .get();
    // }
    @Bean
    public RedisQueueMessageDrivenEndpoint remotePartitioning1WorkInboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint
                = new RedisQueueMessageDrivenEndpoint("remotePartitioning1.master2worker", connectionFactory);
        redisQueueMessageDrivenEndpoint.setOutputChannel(remotePartitioning1WorkInputChannel());
        redisQueueMessageDrivenEndpoint.setExpectMessage(true);
        return redisQueueMessageDrivenEndpoint;
    }

    // @Bean
    // public IntegrationFlow remotePartitioning1WorkOutboundFlow(ConnectionFactory connectionFactory) {
    //     return IntegrationFlows
    //             .from(remotePartitioning1WorkOutputChannel())
    //             .log()
    //             .handle(Jms.outboundAdapter(connectionFactory)
    //                     .destination("remotePartitioning1.worker2master"))
    //             .get();
    // }
    @Bean
    public IntegrationFlow remotePartitioning1WorkOutboundFlow(RedisConnectionFactory connectionFactory) {
        RedisQueueOutboundChannelAdapter redisQueueOutboundChannelAdapter
                = new RedisQueueOutboundChannelAdapter("remotePartitioning1.worker2master", connectionFactory);
        redisQueueOutboundChannelAdapter.setExtractPayload(false);
        return IntegrationFlows
                .from(remotePartitioning1WorkOutputChannel())
                .log()
                .handle(redisQueueOutboundChannelAdapter)
                .get();
    }

}
