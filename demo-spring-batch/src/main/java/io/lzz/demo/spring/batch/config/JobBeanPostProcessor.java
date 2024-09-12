package io.lzz.demo.spring.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.metrics.BatchMetrics;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class JobBeanPostProcessor implements BeanPostProcessor, JobExecutionListener {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractJob) {
            ((AbstractJob) bean).registerJobExecutionListener(this);
        }
        return bean;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("job[{}]开始", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration jobExecutionDuration = BatchMetrics.calculateDuration(jobExecution.getStartTime(), jobExecution.getEndTime());
        log.info("job[{}]结束,执行时间[{}]", jobExecution.getJobInstance().getJobName(),
                BatchMetrics.formatDuration(jobExecutionDuration));
    }

}
