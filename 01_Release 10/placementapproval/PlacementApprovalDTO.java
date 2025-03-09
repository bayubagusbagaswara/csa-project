package com.services.billingservice.dto.placement.placementapproval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApprovalDTO {

    private Long id;

    private String approvalStatus;

    private String inputId;

    private String inputIPAddress;

    private LocalDateTime inputDate;

    private String approveId;

    private String approveIPAddress;

    private LocalDateTime approveDate;

    private String imCode;

    private String imName;

    private String fundCode;

    private String fundName;

    private String placementBankCode;

    private String placementBankName;

    private String placementBankCashAccountName;

    private String placementBankCashAccountNo;

    private String currency;

    private String principle;

    private String placementDate;

    private String referenceNo;

    private String siReferenceID;

    private String accountDebitNo;

    private String biCode;

    private String placementType;

    private String placementProcessType;

    private String placementTransferType;

}
