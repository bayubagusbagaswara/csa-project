package com.services.billingservice.dto.placement.transfersknrtgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BdiXferBeneficieryDTO {

    private String bdiBenfID;

    private String bdiBenfAcct;

    private String bdiBenfName;

    private String bdiBenfAddress;

    private String bdiBenStatus;

    private String bdiBenCitizen;

    private BankInfoDTO bankInfo;

}
