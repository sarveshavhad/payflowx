package com.payflowx.payment_core.exception;

public class TransactionAlreadyRefundedException extends BusinessException {

    public TransactionAlreadyRefundedException(String message) {

        super(message);
    }
}
