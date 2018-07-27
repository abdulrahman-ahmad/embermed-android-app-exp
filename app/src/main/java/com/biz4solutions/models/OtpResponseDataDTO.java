package com.biz4solutions.models;

/*
 * Created by ketan on 12/8/2017.
 */

public class OtpResponseDataDTO {
    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "OtpRequestDTO{" +
                "transactionId='" + transactionId + '\'' +
                '}';
    }
}
