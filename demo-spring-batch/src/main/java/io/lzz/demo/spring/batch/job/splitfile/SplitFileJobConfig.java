package io.lzz.demo.spring.batch.job.splitfile;

import io.lzz.demo.spring.batch.entity.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
public class SplitFileJobConfig {

    public static final String FILE_NAME = "split_file.txt";
    public static final String DIR = "./tmp";
    public static final int FILE_NUM = 4;
    @Autowired
    private TaskExecutor batchTaskExecutor;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private TaskExecutor stepTaskExecutor;

    @Bean
    public Job splitFileJob() {
        return jobBuilderFactory.get("splitFileJob")
                .start(splitFileJobInitStep())
                .next(splitFileJobStep())
                .build();
    }

    private Step splitFileJobInitStep() {
        return stepBuilderFactory.get("splitFileJobInitStep")
                .tasklet((contribution, chunkContext) -> {
                    File dir = new File(DIR);
                    File file = new File(dir, FILE_NAME);
                    if (!file.exists()) {
                        createFile(dir, file);
                    }
                    createFile(dir, file);

                    Arrays.stream(Objects.requireNonNull(
                                    dir.listFiles((dir2, name) -> name.matches(FILE_NAME + "\\.tmp\\." + "\\d"))))
                            .forEach(File::delete);

                    try (
                            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
                    ) {
                        List<Writer> writerList = new ArrayList<>();
                        for (int i = 0; i < FILE_NUM; i++) {
                            File file1 = new File(dir, FILE_NAME + ".tmp." + i);
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file1));
                            writerList.add(writer);
                        }

                        String line;
                        while ((line = lineNumberReader.readLine()) != null) {
                            Writer writer = writerList.get(lineNumberReader.getLineNumber() % FILE_NUM);
                            writer.write(line);
                            writer.write(System.lineSeparator());
                        }

                        writerList.forEach(this::close);
                    }

                    return null;
                })
                .build();

    }

    @Bean
    public Step splitFileJobStep() {
        return stepBuilderFactory.get("splitFileJobStep")
                .partitioner(splitFileJobWorkStep())
                .partitioner(splitFileJobWorkStep().getName(), splitFileJobPartitioner())
                .taskExecutor(stepTaskExecutor)
                .build();
    }

    private void createFile(File dir, File file) throws IOException {
        dir.mkdirs();
        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        ) {
            // for (int i = 1; i <= 40_000_000; i++) {
            for (int i = 1; i <= 10; i++) {
                writer.write(i + "|" + "userName" + i + "|" + LocalDateTime.now() + "|");
                writer.write(i + "|" + "userName" + i + "|" + LocalDateTime.now() + "|");
                writer.write(i + "|" + "userName" + i + "|" + LocalDateTime.now() + "|");
                writer.newLine();
            }
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    private Step splitFileJobWorkStep() {
        return stepBuilderFactory.get("splitFileJobWorkStep")
                .<User, User>chunk(3)
                .reader(reader(null))
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(readerListener())
                .taskExecutor(batchTaskExecutor)
                .build();
    }

    @SneakyThrows
    @StepScope
    @Bean
    public Partitioner splitFileJobPartitioner() {
        MultiResourcePartitioner multiResourcePartitioner = new MultiResourcePartitioner();
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = patternResolver.getResources("file:" + DIR + File.separator + FILE_NAME + ".tmp." + "*");
        multiResourcePartitioner.setResources(resources);
        return multiResourcePartitioner;
    }

    // private ItemReader<User> reader() {
    //     String[] names = {"id", "userName", "createTime"};
    //     return new FlatFileItemReaderBuilder<User>()
    //             .name("reader")
    //             .resource(new FileSystemResource(new File(DIR, FILE_NAME)))
    //             .delimited()
    //             .delimiter("|")
    //             .names(names)
    //             .targetType(User.class)
    //             .build();
    // }
    @StepScope
    @Bean
    public FlatFileItemReader<User> reader(@Value("#{stepExecutionContext['fileName']}") String fileName) {
        return new FlatFileItemReaderBuilder<User>()
                .name("reader")
                .resource(new FileSystemResource(fileName.replaceFirst("file:", "")))
                .lineMapper(new LineMapper<User>() {
                    @Override
                    public User mapLine(String line, int lineNumber) throws Exception {
                        log.info("reader: {}", line);
                        String[] split = line.split("\\|");
                        return new User()
                                .setId(Integer.valueOf(split[0]))
                                .setUserName(split[1])
                                .setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(split[2]));
                    }
                })
                .build();
    }

    private ItemProcessor<? super User, ? extends User> processor() {
        return (ItemProcessor<User, User>) item -> {
            log.info("processor: {}", item);
            return item;
        };
    }

    private ItemWriter<? super User> writer() {
        return (ItemWriter<User>) items -> log.info("writer: {}", items);
    }

    private ItemReadListener<User> readerListener() {
        return new ItemReadListener<User>() {
            @Override
            public void beforeRead() {
            }

            @Override
            public void afterRead(User item) {
            }

            @Override
            public void onReadError(Exception ex) {
                log.error("", ex);
            }
        };
    }

}
