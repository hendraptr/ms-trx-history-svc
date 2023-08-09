package com.assessment.mstrxhistsvc.controllers;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.services.TransactionHistoryService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class TransactionHistoryControllerTests {

  @InjectMocks
  private TransactionHistoryController transactionHistoryController;

  @Mock
  private TransactionHistoryService transactionHistoryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getTransactionsTest() {
    TransactionHistory trx = new TransactionHistory();
    Page<TransactionHistory> page = new PageImpl<>(Collections.singletonList(trx));

    when(transactionHistoryService.getTransactionsByFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<Page<TransactionHistory>> response = transactionHistoryController.getTransactions(null, null, null, Pageable.unpaged());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getContent()).hasSize(1);
  }

  @Test
  void getTransactionByIdNotFoundTest() {
    when(transactionHistoryService.getTransactionById(any(UUID.class))).thenReturn(Optional.empty());

    ResponseEntity<TransactionHistory> response = transactionHistoryController.getTransactionById(UUID.randomUUID());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getTransactionByIdFoundTest() {
    TransactionHistory trx = new TransactionHistory();
    when(transactionHistoryService.getTransactionById(any(UUID.class))).thenReturn(Optional.of(trx));

    ResponseEntity<TransactionHistory> response = transactionHistoryController.getTransactionById(UUID.randomUUID());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void createTransactionTest() {
    TransactionHistory trx = new TransactionHistory();
    when(transactionHistoryService.createTransaction(any(TransactionHistory.class))).thenReturn(trx);

    ResponseEntity<TransactionHistory> response = transactionHistoryController.createTransaction(trx);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void updateTransactionNotFoundTest() {
    // When the service layer throws an IllegalArgumentException
    doThrow(new IllegalArgumentException()).when(transactionHistoryService).updateTransaction(any(TransactionHistory.class));

    ResponseEntity<String> response = transactionHistoryController.updateTransaction(UUID.randomUUID(), new TransactionHistory(), 1);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void updateTransactionVersionMismatchTest() {
    // When the service layer throws an OptimisticLockException
    doThrow(new OptimisticLockException()).when(transactionHistoryService).updateTransaction(any(TransactionHistory.class));

    ResponseEntity<String> response = transactionHistoryController.updateTransaction(UUID.randomUUID(), new TransactionHistory(), 1);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
    assertThat(response.getBody()).isEqualTo("Concurrency conflict: Version mismatch.");
  }

  @Test
  void updateTransactionSuccessTest() {
    ResponseEntity<String> response = transactionHistoryController.updateTransaction(UUID.randomUUID(), new TransactionHistory(), 1);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteTransactionNotFoundTest() {
    when(transactionHistoryService.deleteTransaction(any(UUID.class))).thenReturn(false);

    ResponseEntity<Void> response = transactionHistoryController.deleteTransaction(UUID.randomUUID());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteTransactionSuccessTest() {
    when(transactionHistoryService.deleteTransaction(any(UUID.class))).thenReturn(true);

    ResponseEntity<Void> response = transactionHistoryController.deleteTransaction(UUID.randomUUID());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
