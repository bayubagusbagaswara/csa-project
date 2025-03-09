package com.services.billingservice.dto.placement.transfersknrtgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XferInfoFromDTO {

    private String acctId;

    private String acctType;

    private String acctCur;

    private String bdiAcctStatus;

    private String bdiAcctCitizen;

}
