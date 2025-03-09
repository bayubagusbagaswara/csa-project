package com.services.billingservice.dto.placement.overbookingcasa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcctIdFromDTO {

    private String aTMCardNo;

    private String acctIdF;

    private String acctTypeF;

    private String costCtrF;
}
