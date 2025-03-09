package com.services.billingservice.dto.placement.inquiryaccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAccountDataDTO {

    private String sttlmDate;

    private String benefAcctName;

    private String benefAcctNo;

    private String benefAcctType;

    private String benefType;

    private String benefId;

    private String benefResidentStatus;

    private String benefCityCode;

    private String userRefNoBi; // success or failed is autofill

}
