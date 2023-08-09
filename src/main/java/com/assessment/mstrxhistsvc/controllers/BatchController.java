package com.assessment.mstrxhistsvc.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ms-trx-history-svc/batch")
@Slf4j
public class BatchController {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job job;

  @PostMapping
  public void importDataJob(@RequestParam String inputFile) {
    JobParameters jobParameters = new JobParametersBuilder().addString("fileInput", inputFile)
        .addLong("startAt", System.currentTimeMillis()).toJobParameters();
    try {
      jobLauncher.run(job, jobParameters);
    } catch (JobExecutionAlreadyRunningException | JobRestartException |
             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      log.error(e.getMessage());
    }
  }

}
