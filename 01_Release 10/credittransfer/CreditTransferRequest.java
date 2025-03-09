package com.services.billingservice.dto.placement.credittransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransferRequest {

    private String trxType;
    private String category;
    private String sttlmAmt;
    private String sttlmCcy;
    private String sttlmDate;
    private String feeAmt;
    private String chargeBearerCode;
    private String senderAcctNo;
    private String senderAcctType;
    private String senderBic;
    private String benefBic;
    private String benefName;
    private String benefId;
    private String benefAcctNo;
    private String benefAcctType;
    private String proxyType;
    private String proxyValue;
    private String description;
    private String benefType;
    private String benefResidentStatus;
    private String benefCityCode;
    private String purposeTransaction;
    private String cardNo;

}
