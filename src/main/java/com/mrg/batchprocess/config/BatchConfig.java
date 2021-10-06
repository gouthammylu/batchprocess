package com.mrg.batchprocess.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mrg.batchprocess.model.Users;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    private StepBuilderFactory stepBuilderFactory;

    private JobBuilderFactory jobBuilderFactory;

    private DataSource dataSource;

    public BatchConfig(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory, DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    FlatFileItemReader<Users> itemReader() {

        return new FlatFileItemReaderBuilder<Users>()
                .name("userItemReader")
                .resource(new ClassPathResource("test-data.csv"))
                .delimited()
                .delimiter(";")
                .names("id", "name", "surname", "address")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Users>(){{
                    setTargetType(Users.class);
                }})
                .build();
    }

    @Bean
    JdbcBatchItemWriter<Users> itemWriter() {
        return new JdbcBatchItemWriterBuilder<Users>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO USERS (id, name, surname, address) VALUES (:id, :name, :surname, :address)")
                .build();
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step")
                .<Users, Users>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }
}
