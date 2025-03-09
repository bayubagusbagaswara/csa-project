package com.services.billingservice.dto.placement.overbookingcasa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XferInfoDTO {

    private String xferAmt;

    private String xferdesc1;

    private String xferdesc2;
}
