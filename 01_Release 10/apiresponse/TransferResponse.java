package com.services.billingservice.dto.placement.apiresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private String responseCode;

    private String responseMessage;

    private String responseJson;

    private String transferType; // BI-FAST, SKN-RTGS

}
