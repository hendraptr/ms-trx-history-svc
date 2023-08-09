package com.assessment.mstrxhistsvc.services;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.repositories.TransactionHistoryRepository;
import jakarta.persistence.OptimisticLockException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionHistoryService {

  @Autowired
  private TransactionHistoryRepository transactionHistoryRepository;

  @Cacheable(value = "transactionCache", key = "#pageable")
  public Page<TransactionHistory> getAllTransactions(Pageable pageable) {
    log.trace("Fetching all transactions");
    return transactionHistoryRepository.findAll(pageable);
  }

  @Cacheable(value = "transactionCache", key = "#id")
  public Optional<TransactionHistory> getTransactionById(UUID id) {
    log.trace("Fetching transaction with ID: {}", id);
    return transactionHistoryRepository.findById(id);
  }

  @CacheEvict(value = "transactionCache", allEntries = true)
  public TransactionHistory createTransaction(TransactionHistory transaction) {
    log.trace("Creating a new transaction");
    return transactionHistoryRepository.save(transaction);
  }

  @CacheEvict(value = "transactionCache", allEntries = true)
  public void updateTransaction(TransactionHistory updatedTransaction) {
    UUID id = updatedTransaction.getId();
    log.trace("Updating transaction with ID: {}", id);
    int providedVersion = updatedTransaction.getVersion();

    Optional<TransactionHistory> currentTransactionOptional = transactionHistoryRepository.findById(
        id);

    if (currentTransactionOptional.isEmpty()) {
      throw new IllegalArgumentException("Transaction not found");
    }

    TransactionHistory currentTransaction = currentTransactionOptional.get();

    if (currentTransaction.getVersion() != providedVersion) {
      log.warn("Concurrency conflict for transaction with ID: {}. Version mismatch", id);
      throw new OptimisticLockException("Concurrency conflict: Version mismatch");
    }

    transactionHistoryRepository.save(updatedTransaction);
  }

  @CacheEvict(value = "transactionCache", allEntries = true)
  public boolean deleteTransaction(UUID id) {
    log.trace("Deleting transaction with ID: {}", id);
    if (transactionHistoryRepository.existsById(id)) {
      transactionHistoryRepository.deleteById(id);
      return true;
    } else {
      log.warn("Transaction not found for deletion with ID: {}", id);
      return false;
    }
  }

  @Cacheable(value = "transactionCache", key = "{#account, #customerId, #description, #pageable}")
  public Page<TransactionHistory> getTransactionsByFilters(String account, String customerId,
      String description, Pageable pageable) {
    log.trace("Fetching transactions by filters - account: {}, customerId: {}, description: {}",
        account, customerId, description);
    if (account != null && customerId != null && description != null) {
      return transactionHistoryRepository.findByAccountNumberAndCustomerIdAndDescriptionContainingIgnoreCase(
          account, customerId, description, pageable);
    } else if (account != null && customerId != null) {
      return transactionHistoryRepository.findByAccountNumberAndCustomerId(account, customerId,
          pageable);
    } else if (account != null) {
      return transactionHistoryRepository.findByAccountNumber(account, pageable);
    } else if (customerId != null && description != null) {
      return transactionHistoryRepository.findByCustomerIdAndDescriptionContainingIgnoreCase(
          customerId, description, pageable);
    } else if (customerId != null) {
      return transactionHistoryRepository.findByCustomerId(customerId, pageable);
    } else if (description != null) {
      return transactionHistoryRepository.findByDescriptionContainingIgnoreCase(description,
          pageable);
    } else {
      return transactionHistoryRepository.findAll(pageable);
    }
  }


}
