package com.payflowx.payment_core.transaction.dto;


import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionFilterRequest {

    public TransactionType type;

    public TransactionStatus status;

}
