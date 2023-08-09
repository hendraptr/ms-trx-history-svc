package com.assessment.mstrxhistsvc.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BatchControllerTest {

  @InjectMocks
  private BatchController batchController;

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job job;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testImportDataJob() throws JobExecutionAlreadyRunningException,
      JobRestartException,
      JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    JobExecution mockJobExecution = mock(JobExecution.class);
    when(jobLauncher.run(eq(job), any())).thenReturn(mockJobExecution);

    batchController.importDataJob("mockFilePath"); // Pass a mock file path

    verify(jobLauncher).run(eq(job), any());
  }


  @Test
  void testImportDataJobWithExecutionAlreadyRunningException() throws JobExecutionAlreadyRunningException,
      JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

    when(jobLauncher.run(eq(job), any())).thenThrow(JobExecutionAlreadyRunningException.class);
    batchController.importDataJob("mockFilePath");
    verify(jobLauncher).run(eq(job), any());
  }


  @Test
  void testImportDataJobWithRestartException() throws JobExecutionAlreadyRunningException,
      JobRestartException,
      JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    when(jobLauncher.run(eq(job), any())).thenThrow(JobRestartException.class);
    batchController.importDataJob("mockFilePath");
    verify(jobLauncher).run(eq(job), any());
  }

  @Test
  void testImportDataJobWithInstanceAlreadyCompleteException() throws JobExecutionAlreadyRunningException,
      JobRestartException,
      JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    when(jobLauncher.run(eq(job), any())).thenThrow(JobInstanceAlreadyCompleteException.class);
    batchController.importDataJob("mockFilePath");
    verify(jobLauncher).run(eq(job), any());
  }

  @Test
  void testImportDataJobWithParametersInvalidException() throws JobExecutionAlreadyRunningException,
      JobRestartException,
      JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    when(jobLauncher.run(eq(job), any())).thenThrow(JobParametersInvalidException.class);
    batchController.importDataJob("mockFilePath");
    verify(jobLauncher).run(eq(job), any());
  }

}
