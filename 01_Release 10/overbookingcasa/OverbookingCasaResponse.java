package com.services.billingservice.dto.placement.overbookingcasa;

import com.services.billingservice.dto.placement.apiresponse.SubStatusProviderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverbookingCasaResponse {

    private String responseCode;

    private String responseMessage;

    private SubStatusProviderDTO subStatusProvider;

    private OverbookingCasaDataDTO data;

}
