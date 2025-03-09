package com.services.billingservice.dto.placement.overbookingcasa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverbookingCasaRequest {

    private List<HeaderDTO> header;

    private BodyDTO body;

}
