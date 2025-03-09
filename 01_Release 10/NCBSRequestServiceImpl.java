package com.services.billingservice.service.placement.impl;

import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.ncbsrequest.CreateNCBSRequest;
import com.services.billingservice.dto.placement.overbookingcasa.*;
import com.services.billingservice.dto.placement.transfersknrtgs.*;
import com.services.billingservice.exception.GeneralException;
import com.services.billingservice.model.placement.NCBSRequest;
import com.services.billingservice.model.placement.PlacementApproval;
import com.services.billingservice.repository.placement.NCBSRequestRepository;
import com.services.billingservice.service.placement.NCBSRequestService;
import com.services.billingservice.service.placement.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class NCBSRequestServiceImpl implements NCBSRequestService {

    private final NCBSRequestRepository ncbsRequestRepository;
    private final WebClientService webClientService;

    @Override
    public NCBSRequest saveToDatabase(CreateNCBSRequest createNCBSRequest) {
        log.error("Start save NCBS Request to database: {}", createNCBSRequest);

        NCBSRequest ncbsRequest = NCBSRequest.builder()
                .createdDate(createNCBSRequest.getCreatedDate())
                .placementId(createNCBSRequest.getPlacementId())
                .placementType(createNCBSRequest.getPlacementType())
                .placementProcessType(createNCBSRequest.getPlacementProcessType())
                .placementTransferType(createNCBSRequest.getPlacementTransferType())
                .requestJson(createNCBSRequest.getRequestJson())
                .build();

        NCBSRequest save = ncbsRequestRepository.save(ncbsRequest);
        log.info("Successfully save NCBS Request with id: {}", save.getId());
        return save;
    }

    @Override
    public InquiryAccountResponse inquiryAccount(InquiryAccountRequest inquiryAccountRequest) {
        try {
            Mono<InquiryAccountResponse> inquiryAccountResponseMono = webClientService.inquiryAccount(inquiryAccountRequest);
            return inquiryAccountResponseMono.block();
        } catch (WebClientResponseException e) {
            log.error("[Inquiry Account] Error response from server: {}", e.getResponseBodyAsString(), e);
            throw new GeneralException("[Inquiry Account] Server returned an error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("[Inquiry Account] Error when inquiring account: {}", e.getMessage(), e);
            throw new GeneralException("[Inquiry Account] An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public OverbookingCasaResponse overbookingCasa(OverbookingCasaRequest overbookingCasaRequest) {
        try {
            Mono<OverbookingCasaResponse> overbookingCasaResponseMono = webClientService.overbookingCasa(overbookingCasaRequest);
            return overbookingCasaResponseMono.block();
        } catch (WebClientResponseException e) {
            log.error("[Overbooking Casa] Error response from server: {}", e.getResponseBodyAsString(), e);
            throw new GeneralException("[Overbooking Casa] Server returned an error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("[Overbooking Casa] Error when inquiring account: {}", e.getMessage(), e);
            throw new GeneralException("[Overbooking Casa] An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public TransferSknRtgsResponse transferSknRtgs(TransferSknRtgsRequest transferSknRtgsRequest) {
        try {
            Mono<TransferSknRtgsResponse> transferSknRtgsResponseMono = webClientService.transferSknRtgs(transferSknRtgsRequest);
            return transferSknRtgsResponseMono.block();
        } catch (WebClientResponseException e) {
            log.error("[Transfer Skn Rtgs] Error response from server: {}", e.getResponseBodyAsString(), e);
            throw new GeneralException("[Transfer Skn Rtgs] Server returned an error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("[Transfer Skn Rtgs] Error when inquiring account: {}", e.getMessage(), e);
            throw new GeneralException("[Transfer Skn Rtgs] An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public CreditTransferResponse creditTransfer(CreditTransferRequest creditTransferRequest) {
        try {
            Mono<CreditTransferResponse> creditTransferResponseMono = webClientService.creditTransferBiFast(creditTransferRequest);
            return creditTransferResponseMono.block();
        } catch (WebClientResponseException e) {
            log.error("[Credit Transfer] Error response from server: {}", e.getResponseBodyAsString(), e);
            throw new GeneralException("[Credit Transfer] Server returned an error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("[Credit Transfer] Error when inquiring account: {}", e.getMessage(), e);
            throw new GeneralException("[Credit Transfer] An unexpected error occurred: " + e.getMessage());
        }
    }

}
