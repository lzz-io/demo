package io.lzz.demo.spring.batch.job.remotepartitioning;

import io.lzz.demo.spring.batch.component.BasicPartitioner;
import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Slf4j
@Configuration
public class RemotePartitioningJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private MessageChannelPartitionHandler messageChannelPartitionHandler;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private UserRepository userRepository;

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
        messageChannelPartitionHandler.setStepName("remotePartitioningWorkStep");
        return stepBuilderFactory.get("remotePartitioningManagerStep")
                .partitioner(remotePartitioningWorkStep())
                .partitioner(remotePartitioningWorkStep().getName(), getPartitioner())
                .partitionHandler(messageChannelPartitionHandler)
                .build();
    }

    @Bean
    public Step remotePartitioningWorkStep() {
        return stepBuilderFactory.get("remotePartitioningWorkStep")
                .tasklet(remotePartitioningWorkStepTasklet())
                .build();
    }

    private BasicPartitioner getPartitioner() {
        return new BasicPartitioner();
    }

    @StepScope
    @Bean
    public Tasklet remotePartitioningWorkStepTasklet() {
        return (contribution, chunkContext) -> {
            // log.info("contribution={}, chunkContext={}", contribution, chunkContext);
            String keyName = getPartitioner().getKeyName();
            log.info("keyName[{}]={}", keyName, contribution.getStepExecution().getExecutionContext().get(keyName));
            // log.info("keyName={}", chunkContext.getStepContext().getStepExecutionContext().get(keyName));
            return null;
        };
    }

}
