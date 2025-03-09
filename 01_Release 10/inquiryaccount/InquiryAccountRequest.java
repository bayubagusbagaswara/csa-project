package com.services.billingservice.dto.placement.inquiryaccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAccountRequest {

    private String sttlmAmt; // Conditional

    private String sttlmCcy; // Conditional

    private String chargeBearerCode; // Conditional

    private String senderBic; // Mandatory

    private String senderAcctNo; // Mandatory

    private String benefBic; // Mandatory

    private String benefAcctNo; // Mandatory

    private String purposeTransaction; // Mandatory

}
