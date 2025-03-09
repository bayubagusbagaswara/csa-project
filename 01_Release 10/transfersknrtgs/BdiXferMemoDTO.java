package com.services.billingservice.dto.placement.transfersknrtgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BdiXferMemoDTO {

    private String bdiFrMemo1;

    private String bdiFrMemo2;

    private String bdiToMemo1;

    private String bdiToMemo2;

}
