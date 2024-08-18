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

package io.lzz.demo.spring.batch.step2.master;

import io.lzz.demo.spring.batch.entity.User;
import io.lzz.demo.spring.batch.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RemoteChunking master配置
 *
 * @author q1219331697
 */
@Configuration
public class Step2MasterConfig {

    @SuppressWarnings("unused")
    @Autowired
    private TaskExecutor batchTaskExecutor;
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory;
    @Autowired
    private Step2MasterChunkListener step2MasterChunkListener;
    @Autowired
    private Step2MasterExecutionListener step2MasterExecutionListener;
    @Autowired
    private Step2MasterItemProcessListener step2MasterItemProcessListener;
    @Autowired
    private Step2MasterItemReadListener step2MasterItemReadListener;
    @Autowired
    private Step2MasterItemWriteListener step2MasterItemWriteListener;
    @Autowired
    private Step2MasterSkipListener step2MasterSkipListener;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private UserRepository userRepository;

    @Bean
    public Job step2Job() {
        return jobBuilderFactory.get("step2Job")
                .start(initStep())
                .next(step2MasterStep())
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

    // 配合@EnableBatchIntegration
    @Bean
    public TaskletStep step2MasterStep() {
        return managerStepBuilderFactory.<User, User>get("step2MasterStep")//
                .chunk(3)//
                .reader(step2MasterItemReader())//
                .outputChannel(step2MasterOutputChannel())//
                .inputChannel(step2MasterInputChannel())//
                .maxWaitTimeouts(30000)//
                // .messagingTemplate(messagingTemplate)//
                // .faultTolerant()// 失败处理
                // .retryLimit(3)// 重试次数
                // .retry(Exception.class)// 重试异常必须配置
                // .skipLimit(Integer.MAX_VALUE)//
                // .skip(Exception.class)// 跳过异常，通常用自定义异常
                // .noSkip(FileNotFoundException.class)// 哪些异常不跳过
                .listener(step2MasterChunkListener)//
                .listener(step2MasterItemReadListener)//
                .listener(step2MasterItemProcessListener)//
                .listener(step2MasterItemWriteListener)//
                .listener(step2MasterExecutionListener)//
                .listener(step2MasterSkipListener)//
                // .taskExecutor(batchTaskExecutor)// 多线程
                // .throttleLimit(8)// 最大使用线程池数目
                // .allowStartIfComplete(true)// 重新启动已完成 Step
                .build();
    }

    // ItemReader
    @SneakyThrows
    @Bean
    public ItemReader<User> step2MasterItemReader() {
        JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
        reader.setName("step2MasterItemReader");
        reader.setDataSource(dataSource);
        reader.setQueryProvider(step2MasterQueryProvider().getObject());
        reader.setRowMapper(new BeanPropertyRowMapper<>(User.class));
        reader.setPageSize(5);
        return reader;
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public MessageChannel step2MasterOutputChannel() {
        return new DirectChannel();
        // return new ExecutorChannel(taskExecutor);
    }

    // @Autowired
    // @Qualifier("step2MasterQueryProvider")
    // private PagingQueryProvider step2MasterQueryProvider;

    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public PollableChannel step2MasterInputChannel() {
        return new QueueChannel();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean step2MasterQueryProvider() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();

        provider.setDataSource(dataSource);
        provider.setSelectClause("select id, user_name, create_time");
        provider.setFromClause("from t_user");
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        provider.setSortKeys(sortKeys);

        return provider;
    }

    @Bean
    public IntegrationFlow masterOutboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows.from(step2MasterOutputChannel())//
                .handle(Jms.outboundAdapter(connectionFactory)//
                        .destination("batch.step2.master2worker"))
                .get();
    }

    @Bean
    public IntegrationFlow masterInboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows//
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)//
                        .destination("batch.step2.worker2master"))
                .channel(step2MasterInputChannel())//
                .get();
    }

}
