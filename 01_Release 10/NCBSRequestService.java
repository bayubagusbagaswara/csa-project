package com.services.billingservice.service.placement;

import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.ncbsrequest.CreateNCBSRequest;
import com.services.billingservice.dto.placement.overbookingcasa.OverbookingCasaRequest;
import com.services.billingservice.dto.placement.overbookingcasa.OverbookingCasaResponse;
import com.services.billingservice.dto.placement.transfersknrtgs.TransferSknRtgsRequest;
import com.services.billingservice.dto.placement.transfersknrtgs.TransferSknRtgsResponse;
import com.services.billingservice.model.placement.NCBSRequest;

public interface NCBSRequestService {

    InquiryAccountResponse inquiryAccount(InquiryAccountRequest inquiryAccountRequest);

    OverbookingCasaResponse overbookingCasa(OverbookingCasaRequest overbookingCasaRequest);

    TransferSknRtgsResponse transferSknRtgs(TransferSknRtgsRequest transferSknRtgsRequest);

    CreditTransferResponse creditTransfer(CreditTransferRequest creditTransferRequest);

    NCBSRequest saveToDatabase(CreateNCBSRequest createNCBSRequest);
}
