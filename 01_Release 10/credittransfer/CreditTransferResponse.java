package com.services.billingservice.dto.placement.credittransfer;

import com.services.billingservice.dto.placement.apiresponse.SubStatusProviderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransferResponse {

    private String responseCode;

    private String responseMessage;

    private CreditTransferDataDTO data;

    private SubStatusProviderDTO subStatusProvider;

}
