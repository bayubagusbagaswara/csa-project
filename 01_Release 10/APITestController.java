package com.services.billingservice.controller.placement;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.overbookingcasa.OverbookingCasaRequest;
import com.services.billingservice.dto.placement.overbookingcasa.OverbookingCasaResponse;
import com.services.billingservice.dto.placement.transfersknrtgs.TransferSknRtgsRequest;
import com.services.billingservice.dto.placement.transfersknrtgs.TransferSknRtgsResponse;
import com.services.billingservice.service.placement.NCBSRequestService;
import com.services.billingservice.service.placement.PlacementApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/placement/api-test")
@Slf4j
@RequiredArgsConstructor
public class APITestController {

    private final NCBSRequestService ncbsRequestService;
    private final PlacementApprovalService placementApprovalService;

    @PostMapping(path = "/inquiry-account")
    public ResponseEntity<ResponseDTO<InquiryAccountResponse>> inquiryAccount(@RequestBody InquiryAccountRequest inquiryAccountRequest) {
        InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);
        ResponseDTO<InquiryAccountResponse> response = ResponseDTO.<InquiryAccountResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(inquiryAccountResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/credit-transfer")
    public ResponseEntity<ResponseDTO<CreditTransferResponse>> creditTransfer(@RequestBody CreditTransferRequest creditTransferRequest) {
        CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(creditTransferRequest);
        ResponseDTO<CreditTransferResponse> response = ResponseDTO.<CreditTransferResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(creditTransferResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/overbooking-casa")
    public ResponseEntity<ResponseDTO<OverbookingCasaResponse>> overbookingCasaResponse(@RequestBody OverbookingCasaRequest overbookingCasaRequest) {
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(overbookingCasaRequest);
        ResponseDTO<OverbookingCasaResponse> response = ResponseDTO.<OverbookingCasaResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(overbookingCasaResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/transfer-skn-rtgs")
    public ResponseEntity<ResponseDTO<TransferSknRtgsResponse>> transferSknRtgs(@RequestBody TransferSknRtgsRequest transferSknRtgsRequest) {
        TransferSknRtgsResponse transferSknRtgsResponse = ncbsRequestService.transferSknRtgs(transferSknRtgsRequest);
        ResponseDTO<TransferSknRtgsResponse> response = ResponseDTO.<TransferSknRtgsResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(transferSknRtgsResponse)
                .build();
        return ResponseEntity.ok(response);
    }

}
