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

package io.lzz.demo.spring.batch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author q1219331697
 */
@RestController
@RequestMapping("/job")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JobLauncher jobLauncher;

    @GetMapping(value = "/start/{jobName}")
    public String start(@PathVariable String jobName) {
        long beginTime = System.currentTimeMillis();
        JobParameters jobParameters = new JobParametersBuilder()//
                // 定义作业参数，区分不同作业，可实现作业重复执行
                // .addString("key", new Date().toString())//
                .addDate("key", new Date())//
                .toJobParameters();
        JobExecution jobExecution = new JobExecution(1L);
        try {
            log.info("job start ...开始");
            Job job = applicationContext.getBean(jobName, Job.class);
            jobExecution = jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.error("job已执行", e);
        }
        log.info("job started ...结束");
        return "job start! " + jobExecution.getJobInstance() + " " + (System.currentTimeMillis() - beginTime);
    }


}
