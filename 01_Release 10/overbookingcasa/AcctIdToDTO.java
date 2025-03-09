package com.services.billingservice.dto.placement.overbookingcasa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcctIdToDTO {

    private String acctIdT;

    private String acctTypeT;

    private String costCtrT;
}
