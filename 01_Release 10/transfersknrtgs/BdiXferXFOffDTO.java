package com.services.billingservice.dto.placement.transfersknrtgs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BdiXferXFOffDTO {

    private String bdiXferAmtFrm;

    private String bdiXferAmtFrmLCE;

    private String bdiXferAmtTo;

    private String bdiXferAmtToLCE;

    private String bdiXferType;

    private String bdiXferCurCode;

    private String bdiXRateAmt;

    private String bdiStdRateAmt;

    private String bdiXReffNumber;

    private BdiXferBeneficieryDTO bdiXferBeneficiery;

    private String bdiXferCostCtr;

    private String bdiFeeAmt;

    private String bdiFeeAmtLCE;

    private String bdiFeeProcIr;

    private BdiXferMemoDTO bdiXferMemo;

    private TransInfoDTO transInfo;

    private String lldInfo;
}
