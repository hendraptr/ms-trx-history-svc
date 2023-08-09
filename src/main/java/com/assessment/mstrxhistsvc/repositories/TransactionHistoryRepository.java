package com.assessment.mstrxhistsvc.repositories;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {

  Page<TransactionHistory> findByAccountNumber(String accountNumber, Pageable pageable);

  Page<TransactionHistory> findByCustomerId(String customerId, Pageable pageable);

  Page<TransactionHistory> findByAccountNumberAndCustomerId(String accountNumber, String customerId,
      Pageable pageable);

  Page<TransactionHistory> findByDescriptionContainingIgnoreCase(String description,
      Pageable pageable);

  Page<TransactionHistory> findByAccountNumberAndCustomerIdAndDescriptionContainingIgnoreCase(
      String account, String customerId, String description, Pageable pageable);

  Page<TransactionHistory> findByCustomerIdAndDescriptionContainingIgnoreCase(String customerId,
      String description, Pageable pageable);

}