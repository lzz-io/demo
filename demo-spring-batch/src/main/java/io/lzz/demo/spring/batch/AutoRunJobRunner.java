package io.lzz.demo.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class AutoRunJobRunner implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JobLauncher asyJobLauncher;
    @Autowired
    private JobLauncher synJobLauncher;

    @Override
    public void run(String... args) {
        Arrays.asList(
                        // "step2Job"
                        // "remoteChunkingJob"
                        // "step3Job"
                        // "remotePartitioningJob"
                        "splitFileJob"
                )
                .forEach(jobName -> {
                    try {
                        JobParameters jobParameters = new JobParametersBuilder()
                                .addDate("date", new Date())
                                .toJobParameters();
                        asyJobLauncher.run(applicationContext.getBean(jobName, Job.class), jobParameters);
                        log.info("异步启动job【{}】成功", jobName);
                    } catch (Exception e) {
                        log.error("启动job异常，", e);
                    }
                });
    }
}
