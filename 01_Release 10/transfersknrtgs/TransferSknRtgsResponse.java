package com.services.billingservice.dto.placement.transfersknrtgs;

import com.services.billingservice.dto.placement.apiresponse.SubStatusProviderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferSknRtgsResponse {

    private String responseCode;

    private String responseMessage;

    private SubStatusProviderDTO subStatusProvider;

    private TransferSknRtgsDataDTO data;

}
