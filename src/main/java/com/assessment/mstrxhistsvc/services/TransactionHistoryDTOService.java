package com.assessment.mstrxhistsvc.services;

import com.assessment.mstrxhistsvc.mapper.TransactionHistoryMapper;
import com.assessment.mstrxhistsvc.models.dto.TransactionHistoryDTO;
import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.repositories.TransactionHistoryRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TransactionHistoryDTOService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Cacheable(value = "transactionCache", key = "#pageable")
    public Page<TransactionHistoryDTO> getAllTransactions(Pageable pageable) {
        log.trace("Fetching all transactions");
        Page<TransactionHistory> transactions = transactionHistoryRepository.findAll(pageable);
        return transactions.map(TransactionHistoryMapper.INSTANCE::toDTO);  // Convert each entity to DTO
    }

    @Cacheable(value = "transactionCache", key = "#id")
    public Optional<TransactionHistoryDTO> getTransactionById(UUID id) {
        log.trace("Fetching transaction with ID: {}", id);
        Optional<TransactionHistory> trxOptional = transactionHistoryRepository.findById(id);
        return trxOptional.map(TransactionHistoryMapper.INSTANCE::toDTO);

    }

    @CacheEvict(value = "transactionCache", allEntries = true)
    public TransactionHistoryDTO createTransaction(TransactionHistoryDTO transactionDTO) {
        log.trace("Creating a new transaction");

        // Convert DTO to entity
        TransactionHistory transaction = TransactionHistoryMapper.INSTANCE.toEntity(transactionDTO);

        // Save the entity
        TransactionHistory savedTransaction = transactionHistoryRepository.save(transaction);

        // Convert the saved entity back to DTO and return
        return TransactionHistoryMapper.INSTANCE.toDTO(savedTransaction);
    }


    @CacheEvict(value = "transactionCache", allEntries = true)
    public TransactionHistoryDTO updateTransaction(TransactionHistoryDTO updatedTransactionDTO) {
        // Convert DTO to entity
        TransactionHistory updatedTransaction = TransactionHistoryMapper.INSTANCE.toEntity(updatedTransactionDTO);

        UUID id = updatedTransaction.getId();
        log.trace("Updating transaction with ID: {}", id);
        int providedVersion = updatedTransaction.getVersion();

        Optional<TransactionHistory> currentTransactionOptional = transactionHistoryRepository.findById(id);

        if (currentTransactionOptional.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found");
        }

        TransactionHistory currentTransaction = currentTransactionOptional.get();

        if (currentTransaction.getVersion() != providedVersion) {
            log.warn("Concurrency conflict for transaction with ID: {}. Version mismatch", id);
            throw new OptimisticLockException("Concurrency conflict: Version mismatch");
        }

        TransactionHistory savedTransaction = transactionHistoryRepository.save(updatedTransaction);

        // Convert the saved entity back to DTO and return
        return TransactionHistoryMapper.INSTANCE.toDTO(savedTransaction);
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
    public Page<TransactionHistoryDTO> getTransactionsByFilters(String account, String customerId, String description, Pageable pageable) {
        log.trace("Fetching transactions by filters - account: {}, customerId: {}, description: {}", account, customerId, description);

        Page<TransactionHistory> pageResult;

        if (account != null && customerId != null && description != null) {
            pageResult = transactionHistoryRepository.findByAccountNumberAndCustomerIdAndDescriptionContainingIgnoreCase(account, customerId, description, pageable);
        } else if (account != null && customerId != null) {
            pageResult = transactionHistoryRepository.findByAccountNumberAndCustomerId(account, customerId, pageable);
        } else if (account != null) {
            pageResult = transactionHistoryRepository.findByAccountNumber(account, pageable);
        } else if (customerId != null && description != null) {
            pageResult = transactionHistoryRepository.findByCustomerIdAndDescriptionContainingIgnoreCase(customerId, description, pageable);
        } else if (customerId != null) {
            pageResult = transactionHistoryRepository.findByCustomerId(customerId, pageable);
        } else if (description != null) {
            pageResult = transactionHistoryRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        } else {
            pageResult = transactionHistoryRepository.findAll(pageable);
        }

        // Convert each TransactionHistory to TransactionHistoryDTO
        return pageResult.map(TransactionHistoryMapper.INSTANCE::toDTO);
    }

}
