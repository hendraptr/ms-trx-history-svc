package com.assessment.mstrxhistsvc.services;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.repositories.TransactionHistoryRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionHistoryServiceTest {

  @Mock
  TransactionHistoryRepository transactionHistoryRepository;

  @InjectMocks
  TransactionHistoryService transactionHistoryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAllTransactionsTest() {
    Pageable pageable = PageRequest.of(0, 10);
    // Assuming you have a mock of the page to return
    when(transactionHistoryRepository.findAll(pageable)).thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getAllTransactions(pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionByIdFoundTest() {
    UUID id = UUID.randomUUID();
    when(transactionHistoryRepository.findById(id)).thenReturn(Optional.of(new TransactionHistory()));

    Optional<TransactionHistory> result = transactionHistoryService.getTransactionById(id);
    assertTrue(result.isPresent());
  }

  @Test
  void getTransactionByIdNotFoundTest() {
    UUID id = UUID.randomUUID();
    when(transactionHistoryRepository.findById(id)).thenReturn(Optional.empty());

    Optional<TransactionHistory> result = transactionHistoryService.getTransactionById(id);
    assertFalse(result.isPresent());
  }

  @Test
  void createTransactionTest() {
    TransactionHistory transaction = new TransactionHistory();
    when(transactionHistoryRepository.save(transaction)).thenReturn(transaction);

    TransactionHistory result = transactionHistoryService.createTransaction(transaction);
    assertNotNull(result);
  }

  @Test
  void updateTransactionSuccessTest() {
    UUID id = UUID.randomUUID();
    TransactionHistory updatedTransaction = new TransactionHistory();
    updatedTransaction.setId(id);
    updatedTransaction.setVersion(1);

    TransactionHistory currentTransaction = new TransactionHistory();
    currentTransaction.setVersion(1);

    when(transactionHistoryRepository.findById(id)).thenReturn(Optional.of(currentTransaction));
    when(transactionHistoryRepository.save(updatedTransaction)).thenReturn(updatedTransaction);

    assertDoesNotThrow(() -> transactionHistoryService.updateTransaction(updatedTransaction));
  }

  @Test
  void updateTransactionNotFoundTest() {
    UUID id = UUID.randomUUID();
    TransactionHistory updatedTransaction = new TransactionHistory();
    updatedTransaction.setId(id);

    when(transactionHistoryRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> transactionHistoryService.updateTransaction(updatedTransaction));
  }

  @Test
  void updateTransactionVersionMismatchTest() {
    UUID id = UUID.randomUUID();
    TransactionHistory updatedTransaction = new TransactionHistory();
    updatedTransaction.setId(id);
    updatedTransaction.setVersion(2);

    TransactionHistory currentTransaction = new TransactionHistory();
    currentTransaction.setVersion(1);

    when(transactionHistoryRepository.findById(id)).thenReturn(Optional.of(currentTransaction));

    assertThrows(OptimisticLockException.class, () -> transactionHistoryService.updateTransaction(updatedTransaction));
  }

  @Test
  void deleteTransactionSuccessTest() {
    UUID id = UUID.randomUUID();
    when(transactionHistoryRepository.existsById(id)).thenReturn(true);

    assertTrue(transactionHistoryService.deleteTransaction(id));
    verify(transactionHistoryRepository).deleteById(id);
  }

  @Test
  void deleteTransactionNotFoundTest() {
    UUID id = UUID.randomUUID();
    when(transactionHistoryRepository.existsById(id)).thenReturn(false);

    assertFalse(transactionHistoryService.deleteTransaction(id));
    verify(transactionHistoryRepository, never()).deleteById(any());
  }

  @Test
  void getTransactionsByFiltersAccountAndCustomerIdTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findByAccountNumberAndCustomerId(anyString(), anyString(), any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters("account123", "cust123", null, pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionsByFiltersOnlyAccountTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findByAccountNumber(anyString(), any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters("account123", null, null, pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionsByFiltersOnlyCustomerIdTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findByCustomerId(anyString(), any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters(null, "cust123", null, pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionsByFiltersOnlyDescriptionTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findByDescriptionContainingIgnoreCase(anyString(), any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters(null, null, "description", pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionsByFiltersAccountCustomerIdDescriptionTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findByAccountNumberAndCustomerIdAndDescriptionContainingIgnoreCase(anyString(), anyString(), anyString(), any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters("account123", "cust123", "description", pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionsByFiltersCustomerIdAndDescriptionTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findByCustomerIdAndDescriptionContainingIgnoreCase(anyString(), anyString(), any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters(null, "cust123", "description", pageable);
    assertNotNull(result);
  }

  @Test
  void getTransactionsByFiltersNoFiltersTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(transactionHistoryRepository.findAll(any(Pageable.class)))
        .thenReturn(mock(Page.class));

    Page<TransactionHistory> result = transactionHistoryService.getTransactionsByFilters(null, null, null, pageable);
    assertNotNull(result);
  }

}
