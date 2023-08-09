package com.assessment.mstrxhistsvc.config;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.repositories.TransactionHistoryRepository;
import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
@Slf4j
public class BatchConfiguration {

  private TransactionHistoryRepository transactionHistoryRepository;

  @Bean
  @StepScope
  public FlatFileItemReader<TransactionHistory> reader(
      @Value("#{jobParameters['fileInput']}") String filePath) {
    log.info("Configuring FlatFileItemReader for TransactionHistory");
    FlatFileItemReader<TransactionHistory> reader = new FlatFileItemReader<>();
    reader.setLinesToSkip(1);
    reader.setResource(new FileSystemResource(filePath));
    reader.setLineMapper(customLineMapper());
    return reader;
  }

  private LineMapper<TransactionHistory> customLineMapper() {

    DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
    delimitedLineTokenizer.setDelimiter("|");
    delimitedLineTokenizer.setStrict(false);
    delimitedLineTokenizer.setNames("accountNumber", "trxAmount", "description", "trxDate",
        "trxTime", "customerId");

    Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
    customEditors.put(LocalTime.class, new CustomLocalTimeEditor("HH:mm:ss"));
    customEditors.put(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),
        true)); // Replace with your date format

    BeanWrapperFieldSetMapper<TransactionHistory> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(TransactionHistory.class);
    fieldSetMapper.setCustomEditors(customEditors);

    DefaultLineMapper<TransactionHistory> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(delimitedLineTokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    return lineMapper;
  }

  @Bean
  public TransactionHistoryProcessor processor() {
    return new TransactionHistoryProcessor();
  }

  @Bean
  public RepositoryItemWriter<TransactionHistory> writer() {
    RepositoryItemWriter<TransactionHistory> repositoryItemWriter = new RepositoryItemWriter<>();
    repositoryItemWriter.setRepository(transactionHistoryRepository);
    repositoryItemWriter.setMethodName("save");

    return repositoryItemWriter;
  }

  @Bean
  public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("step-1", jobRepository).<TransactionHistory, TransactionHistory>chunk(
            10, transactionManager).reader(reader(null)).processor(processor()).writer(writer())
        .build();
  }

  @Bean
  public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new JobBuilder("importTransactions", jobRepository).flow(
        step1(jobRepository, transactionManager)).end().build();
  }

}
