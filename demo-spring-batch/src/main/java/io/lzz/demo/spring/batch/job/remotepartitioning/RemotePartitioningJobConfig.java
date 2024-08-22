package io.lzz.demo.spring.batch.job.remotepartitioning;

import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
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
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Date;

@Slf4j
@EnableBatchProcessing
@EnableBatchIntegration
@Configuration
public class RemotePartitioningJobConfig {

    public static final int MOD_SIZE = 4;
    @Autowired
    private DataSource dataSource;
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
    public Job remotePartitioningJob() {
        return jobBuilderFactory.get("remotePartitioningJob")
                .start(initStep())
                .next(remotePartitioningManagerStep())
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
    public Step remotePartitioningManagerStep() {
        return this.managerStepBuilderFactory.get("remotePartitioningManagerStep")
                // remotePartitioningWorkerStep，worker的step名字，一定要注意！！！
                .partitioner("remotePartitioningWorkerStep", new RemotePartitioner())
                .gridSize(MOD_SIZE)
                .outputChannel(remotePartitioningManagerOutputChannel()) // requests sent to workers
                .inputChannel(remotePartitioningManagerInputChannel())   // replies received from workers
                .build();
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public DirectChannel remotePartitioningManagerOutputChannel() {
        return new DirectChannel();
    }

    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public DirectChannel remotePartitioningManagerInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow remotePartitioningManagerOutboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(remotePartitioningManagerOutputChannel())
                .log()
                .handle(Jms.outboundAdapter(connectionFactory)//
                        .destination("master2worker"))
                // .destination("batch.remotePartitioning.master2worker"))
                .get();
    }

    @Bean
    public IntegrationFlow remotePartitioningManagerInboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows//
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
                        .destination("worker2master"))
                // .destination("batch.remotePartitioning.worker2master"))
                .channel(remotePartitioningManagerInputChannel())//
                .log()
                .get();
    }

    // worker******************************************************************
    @Bean
    public Step remotePartitioningWorkerStep() {
        return this.workerStepBuilderFactory.get("remotePartitioningWorkerStep")
                .inputChannel(remotePartitioningWorkInputChannel()) // requests received from the manager
                .outputChannel(remotePartitioningWorkOutputChannel()) // replies sent to the manager
                .<User, User>chunk(4)
                .reader(remotePartitioningWorkerStepItemReader(null))
                .processor(remotePartitioningWorkerStepItemProcessor())
                .writer(remotePartitioningWorkerStepItemWriter())
                .build();
    }

    /*
     * Configure inbound flow (requests coming from the manager)
     */
    @Bean
    public DirectChannel remotePartitioningWorkInputChannel() {
        return new DirectChannel();
    }

    /*
     * Configure outbound flow (replies going to the manager)
     */
    @Bean
    public DirectChannel remotePartitioningWorkOutputChannel() {
        return new DirectChannel();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<User> remotePartitioningWorkerStepItemReader(
            @Value("#{stepExecutionContext['mod']}") Integer mod) {
        return new JpaPagingItemReaderBuilder<User>()
                .name("remotePartitioningWorkerStepItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select u from User u where mod(id, " + MOD_SIZE + ") = " + mod)
                // .queryString("select u from User u")
                .pageSize(3)
                .build();
    }

    @Bean
    public ItemProcessor<User, User> remotePartitioningWorkerStepItemProcessor() {
        return item -> {
            item.setUserName(item.getUserName() + "修改");
            return item;
        };
    }

    @Bean
    public ItemWriter<User> remotePartitioningWorkerStepItemWriter() {
        return items -> {
            userRepository.saveAll(items);
            log.info("step3WorkerItemWriter: {}", items);
        };
    }

    @Bean
    public IntegrationFlow remotePartitioningWorkInboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows//
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
                        .destination("master2worker"))
                // .destination("batch.remotePartitioning.master2worker"))
                .channel(remotePartitioningWorkInputChannel())//
                .log()
                .get();
    }

    @Bean
    public IntegrationFlow remotePartitioningWorkOutboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(remotePartitioningWorkOutputChannel())
                .log()
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination("worker2master"))
                // .destination("batch.remotePartitioning.worker2master"))
                .get();
    }

}
