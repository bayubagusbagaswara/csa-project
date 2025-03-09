package com.services.billingservice.dto.placement.transfersknrtgs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransInfoDTO {

    @JsonProperty(value = "trn")
    private String trn;

    @JsonProperty(value = "fee")
    private String fee;

}
