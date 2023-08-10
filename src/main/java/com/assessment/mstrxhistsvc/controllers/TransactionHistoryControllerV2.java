package com.assessment.mstrxhistsvc.controllers;

import com.assessment.mstrxhistsvc.models.dto.TransactionHistoryDTO;
import com.assessment.mstrxhistsvc.services.TransactionHistoryDTOService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/ms-trx-history-svc/v2/transactions")
public class TransactionHistoryControllerV2 {

  @Autowired
  TransactionHistoryDTOService transactionHistoryDTOService;

  @GetMapping
  public ResponseEntity<Page<TransactionHistoryDTO>> getTransactions(
      @RequestParam(required = false) String account,
      @RequestParam(required = false) String customerId,
      @RequestParam(required = false) String description, Pageable pageable) {
    Page<TransactionHistoryDTO> transactions = transactionHistoryDTOService.getTransactionsByFilters(
        account, customerId, description, pageable);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransactionHistoryDTO> getTransactionById(@PathVariable UUID id) {
    Optional<TransactionHistoryDTO> transaction = transactionHistoryDTOService.getTransactionById(id);
    return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<TransactionHistoryDTO> createTransaction(
      @RequestBody TransactionHistoryDTO transaction) {
    TransactionHistoryDTO createdTransaction = transactionHistoryDTOService.createTransaction(
        transaction);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateTransaction(@PathVariable UUID id,
      @RequestBody TransactionHistoryDTO updatedTransaction, @RequestHeader("If-Match") int ifMatch) {
    try {
      updatedTransaction.setId(id);
      updatedTransaction.setVersion(ifMatch);

      transactionHistoryDTOService.updateTransaction(updatedTransaction);

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
    if (transactionHistoryDTOService.deleteTransaction(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
