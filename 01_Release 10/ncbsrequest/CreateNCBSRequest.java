package com.services.billingservice.dto.placement.ncbsrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNCBSRequest {

    private LocalDateTime createdDate;

    private String placementId;

    private String placementType;

    private String placementProcessType;

    private String placementTransferType;

    private String requestJson;

}
