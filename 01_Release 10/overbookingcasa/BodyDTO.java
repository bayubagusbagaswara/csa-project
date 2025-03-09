package com.services.billingservice.dto.placement.overbookingcasa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyDTO {

    private AcctIdFromDTO acctIdFrom;
    private AcctIdToDTO acctIdTo;
    private XferInfoDTO xferInfo;
}
