package com.services.billingservice.dto.placement.credittransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransferDataDTO {

    private String trxType;

    private String payUserRefNo;

    private String infoStatus;

    private String sttlmDate;

    private String benefName;

    private String benefType;

    private String benefId;

    private String benefResidentStatus;

    private String benefCityCode;

    private String cardNo;

    private String feeAmt;

    private String chargeBearerCode;

    private String userRefNoBi;

}
