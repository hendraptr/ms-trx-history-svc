package com.assessment.mstrxhistsvc.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_trx_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionHistory implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  @Column(name = "ACCOUNT_NUMBER")
  private String accountNumber;

  @Column(name = "TRX_AMOUNT")
  private BigDecimal trxAmount;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "TRX_DATE")
  private Date trxDate;

  @Column(name = "TRX_TIME")
  private LocalTime trxTime;

  @Column(name = "CUSTOMER_ID")
  private String customerId;

  @Version
  @Column(name = "VERSION")
  private int version;

}
