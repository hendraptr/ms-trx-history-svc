package com.assessment.mstrxhistsvc.config;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import org.springframework.batch.item.ItemProcessor;

public class TransactionHistoryProcessor implements
    ItemProcessor<TransactionHistory, TransactionHistory> {

  @Override
  public TransactionHistory process(TransactionHistory transactionHistory) throws Exception {
    return transactionHistory;
  }

}
