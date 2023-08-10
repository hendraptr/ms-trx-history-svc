package com.assessment.mstrxhistsvc.models.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionHistoryDTO implements Serializable {

    private UUID id;
    private String accountNumber;
    private BigDecimal trxAmount;
    private String description;
    private Date trxDate;
    private LocalTime trxTime;
    private String customerId;
    private int version;

}
