package com.services.billingservice.dto.placement.transfersknrtgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferSknRtgsRequest {

    private XferInfoFromDTO xferInfoFrom;

    private BdiXferXFOffDTO bdiXferXFOff;

}
