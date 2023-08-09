package com.assessment.mstrxhistsvc.controllers;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.services.TransactionHistoryService;
import jakarta.persistence.OptimisticLockException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ms-trx-history-svc/transactions")
public class TransactionHistoryController {

  @Autowired
  TransactionHistoryService transactionHistoryService;

  @GetMapping
  public ResponseEntity<Page<TransactionHistory>> getTransactions(
      @RequestParam(required = false) String account,
      @RequestParam(required = false) String customerId,
      @RequestParam(required = false) String description, Pageable pageable) {
    Page<TransactionHistory> transactions = transactionHistoryService.getTransactionsByFilters(
        account, customerId, description, pageable);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransactionHistory> getTransactionById(@PathVariable UUID id) {
    Optional<TransactionHistory> transaction = transactionHistoryService.getTransactionById(id);
    return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<TransactionHistory> createTransaction(
      @RequestBody TransactionHistory transaction) {
    TransactionHistory createdTransaction = transactionHistoryService.createTransaction(
        transaction);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateTransaction(@PathVariable UUID id,
      @RequestBody TransactionHistory updatedTransaction, @RequestHeader("If-Match") int ifMatch) {
    try {
      updatedTransaction.setId(id);
      updatedTransaction.setVersion(ifMatch);

      transactionHistoryService.updateTransaction(updatedTransaction);

      return ResponseEntity.ok("Transaction updated successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    } catch (OptimisticLockException e) {
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
          .body("Concurrency conflict: Version mismatch.");
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
    if (transactionHistoryService.deleteTransaction(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
