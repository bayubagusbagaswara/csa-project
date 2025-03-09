package com.services.billingservice.service.placement.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.billingservice.dto.ErrorMessageDTO;
import com.services.billingservice.dto.placement.apiresponse.TransferResponse;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountDataDTO;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.ncbsrequest.CreateNCBSRequest;
import com.services.billingservice.dto.placement.ncbsresponse.CreateNCBSResponse;
import com.services.billingservice.dto.placement.overbookingcasa.*;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalDTO;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalResponse;
import com.services.billingservice.dto.placement.transfersknrtgs.*;
import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.exception.BadRequestException;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.exception.placement.TransferPlacementException;
import com.services.billingservice.mapper.placement.PlacementApprovalMapper;
import com.services.billingservice.model.placement.NCBSRequest;
import com.services.billingservice.model.placement.NCBSResponse;
import com.services.billingservice.model.placement.PlacementData;
import com.services.billingservice.model.placement.PlacementApproval;
import com.services.billingservice.repository.placement.PlacementDataRepository;
import com.services.billingservice.repository.placement.PlacementApprovalRepository;
import com.services.billingservice.service.placement.NCBSRequestService;
import com.services.billingservice.service.placement.NCBSResponseService;
import com.services.billingservice.service.placement.PlacementApprovalService;
import com.services.billingservice.utils.placement.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlacementApprovalServiceImpl implements PlacementApprovalService {

    private static final String PLACEMENT_TYPE_EXTERNAL = "External";
    private static final String PLACEMENT_TYPE_INTERNAL = "Internal";
    private static final String NCBS_STATUS_FAILED = "FAILED";
    private static final String NCBS_STATUS_SUCCESS = "SUCCESS";
    private static final String BI_FAST = "BI-FAST";
    private static final String SKN = "SKN";
    private static final String RTGS = "RTGS";
    private static final String RESPONSE_CODE_SUCCESS = "000";
    private static final String RETRIEVE_PLACEMENT_ERROR = "RETRIEVE_PLACEMENT_ERROR";
    private static final String INQUIRY_ERROR = "INQUIRY_ERROR";
    private static final String PROCESS_PLACEMENT_ERROR = "PROCESS_PLACEMENT_ERROR";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PlacementApprovalRepository placementApprovalRepository;
    private final PlacementDataRepository placementDataRepository;
    private final PlacementApprovalMapper placementApprovalMapper;
    private final NCBSRequestService ncbsRequestService;
    private final NCBSResponseService ncbsResponseService;

    @Transactional
    @Override
    public synchronized PlacementApprovalResponse approve(List<Long> placementApprovalIds, String approveId, String approveIPAddress) {
        log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementApprovalIds, approveId, approveIPAddress);
        int totalSuccess = 0;
        int totalFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (Long placementApprovalId : placementApprovalIds) {
            // **Step 1: Retrieve Placement Approval**
            PlacementApproval placementApproval = placementApprovalRepository.findById(placementApprovalId)
                    .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementApprovalId));

            // **Step 2: Process Transfer**
            try {
                if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
                    totalSuccess += processInternalPlacement(placementApproval, errorMessageDTOList);

                } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {

                    InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
                    log.info("[Inquiry Account] Request created: {}", inquiryAccountRequest);

                    InquiryAccountResponse inquiryAccountResponse = validateInquiryAccount(inquiryAccountRequest, placementApproval, errorMessageDTOList);

                    if (!"200R000000".equals(inquiryAccountResponse.getResponseCode())) {

                        placementApproval.setApprovalStatus(ApprovalStatus.Pending);
                        placementApproval.setApproverId(approveId);
                        placementApproval.setApproveIPAddress(approveIPAddress);
                        placementApproval.setApproveDate(LocalDateTime.now());
                        placementApproval.setNcbsStatus(NCBS_STATUS_FAILED);
                        placementApproval.setNcbsResponseCode(inquiryAccountResponse.getResponseCode());
                        placementApproval.setNcbsResponseMessage(inquiryAccountResponse.getResponseMessage());

                        placementApprovalRepository.save(placementApproval);

                        log.error("[Inquiry Account] Failed for placement approval ID: {}", placementApproval.getId());
                        return buildPlacementApprovalResponse(totalSuccess, ++totalFailed, errorMessageDTOList);
                    }

                    log.info("[Inquiry Account] Response data: {}", inquiryAccountResponse);
                    InquiryAccountDataDTO inquiryAccountDTO = inquiryAccountResponse.getData();

                    TransferResponse transferResponse = processExternalTransfer(inquiryAccountDTO, placementApproval, errorMessageDTOList);

                    if (!"200R000000".equals(transferResponse.getResponseCode())) {
                        placementApproval.setApprovalStatus(ApprovalStatus.Approved);
                        placementApproval.setApproverId(approveId);
                        placementApproval.setApproveIPAddress(approveIPAddress);
                        placementApproval.setApproveDate(LocalDateTime.now());
                        placementApproval.setNcbsStatus(NCBS_STATUS_FAILED);
                        placementApproval.setNcbsResponseCode(transferResponse.getResponseCode());
                        placementApproval.setNcbsResponseMessage(transferResponse.getResponseMessage());

                        placementApprovalRepository.save(placementApproval);

                        throw new TransferPlacementException(transferResponse.getResponseCode(), transferResponse.getResponseMessage());
                    }

                    // Credit Transfer is Success
                    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
                    placementApproval.setApproverId(approveId);
                    placementApproval.setApproveIPAddress(approveIPAddress);
                    placementApproval.setApproveDate(LocalDateTime.now());
                    placementApproval.setNcbsStatus(NCBS_STATUS_SUCCESS);
                    placementApproval.setNcbsResponseCode(transferResponse.getResponseCode());
                    placementApproval.setNcbsResponseMessage(transferResponse.getResponseMessage());

                    placementApprovalRepository.save(placementApproval);
                    totalSuccess++;
                }
            } catch (Exception e) {
                log.error("Error processing placement transfer for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
                errorMessageDTOList.add(new ErrorMessageDTO(PROCESS_PLACEMENT_ERROR, Collections.singletonList("Error occurred while processing transfer: " + e.getMessage())));
                totalFailed++;
            }
        }

        return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
    }

    @Transactional
    @Override
    public PlacementApprovalResponse reject(List<Long> placementApprovalIds, String approveId, String approveIPAddress) {
        log.info("Start reject placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementApprovalIds, approveId, approveIPAddress);
        int totalSuccess = 0;
        int totalFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (Long placementApprovalId : placementApprovalIds) {
            try {
                PlacementApproval placementApproval = placementApprovalRepository.findById(placementApprovalId)
                        .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementApprovalId));
                placementApproval.setApprovalStatus(ApprovalStatus.Rejected);
                placementApproval.setApproverId(approveId);
                placementApproval.setApproveIPAddress(approveIPAddress);
                placementApproval.setApproveDate(LocalDateTime.now());

                List<PlacementData> placementDataList = placementDataRepository.findByPlacementApprovalId(String.valueOf(placementApprovalId));

                for (PlacementData placementData : placementDataList) {
                    placementData.setPlacementApprovalId("");
                    PlacementData save = placementDataRepository.save(placementData);
                    log.info("Success remove placement approval id from placement data id: {}", save.getId());
                }

                PlacementApproval save = placementApprovalRepository.save(placementApproval);
                log.info("Save reject placement approval: {}", save);
                totalSuccess++;
            } catch (Exception e) {
                log.error("Error when reject placement: {}", e.getMessage(), e);
                totalFailed++;
            }
        }
        return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
    }

    @Override
    public List<PlacementApprovalDTO> getAllByCurrentDateAndApprovalStatus(String approvalStatus) {
        log.info("Start get all placement approval by current date and approval status: {}", approvalStatus);
        try {
            LocalDate date = LocalDate.now();
            ApprovalStatus approvalStatusEnum = ApprovalStatus.Pending;

            if (ApprovalStatus.Approved.getStatus().equalsIgnoreCase(approvalStatus)) {
                approvalStatusEnum = ApprovalStatus.Approved;
            } else if (ApprovalStatus.Rejected.getStatus().equalsIgnoreCase(approvalStatus)) {
                approvalStatusEnum = ApprovalStatus.Rejected;
            }

            List<PlacementApproval> placementApprovalList = placementApprovalRepository.findByPlacementDateAndApprovalStatus(date, approvalStatusEnum);
            return placementApprovalMapper.toDTOList(placementApprovalList);
        } catch (Exception e) {
            log.error("Error when get all placement approval by date and approval status: {}", e.getMessage(), e);
            throw new BadRequestException("Error when get all placement approval by data and approval status: " + e.getMessage());
        }
    }

    @Override
    public PlacementApproval getById(Long id) {
        return placementApprovalRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Placement not found with id: " + id));
    }

    @Override
    public List<PlacementApprovalDTO> getAllByDateAndApprovalStatusIsPending() {
        log.info("Start get all by date and approval status is pending");
        try {
            LocalDate localDate = LocalDate.now();
            ApprovalStatus approvalStatusEnum = ApprovalStatus.Pending;
            List<PlacementApproval> placementApprovalList = placementApprovalRepository.findByPlacementDateAndApprovalStatus(localDate, approvalStatusEnum);
            return placementApprovalMapper.toDTOList(placementApprovalList);
        } catch (Exception e) {
            log.error("Error when get all placement approval by date and approval status pending: {}", e.getMessage(), e);
            throw new BadRequestException("Error when get all placement approval by data and approval status pending: " + e.getMessage());
        }
    }

    private InquiryAccountResponse validateInquiryAccount(InquiryAccountRequest inquiryAccountRequest, PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
        log.info("Start validateInquiryAccount for placementId; {}", placementApproval.getId());

        InquiryAccountResponse inquiryAccountResponse = new InquiryAccountResponse();
        try {
            // **1: Save request object to the table NCBS Request**
            saveNCBSRequest(placementApproval, convertObjectToJson(inquiryAccountRequest));
            log.info("Saved NCBS request for placementApprovalId: {}", placementApproval.getId());

            // **2: Send request to NCBS API**
            inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);
            log.debug("Received InquiryAccountResponse: {}", inquiryAccountResponse);

            // **3: Check response**
            if (!"200R000000".equals(inquiryAccountResponse.getResponseCode())) {
                // jika responseCode selain Success, maka kita tambahkan pesan error di errorMessageDTOList
                errorMessageDTOList.add(new ErrorMessageDTO(inquiryAccountResponse.getResponseCode(), Collections.singletonList(inquiryAccountResponse.getResponseMessage())));
            }

            // **4: Save response object to the table NCBS Response**
            String responseJson = convertObjectToJson(inquiryAccountResponse);

            saveNCBSResponse(placementApproval, inquiryAccountResponse.getResponseCode(), inquiryAccountResponse.getResponseMessage(), responseJson, true);

        } catch (HttpClientErrorException e) {
            log.error("HTTP request error during validateInquiryAccount for placementApprovalId {}: {}", placementApproval.getId(), e.getMessage(), e);
            errorMessageDTOList.add(new ErrorMessageDTO(INQUIRY_ERROR, Collections.singletonList("HTTP error: " + e.getMessage())));
        } catch (Exception e) {
            log.error("Unexpected error during validateInquiryAccount for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
            errorMessageDTOList.add(new ErrorMessageDTO(INQUIRY_ERROR, Collections.singletonList("Unexpected error: " + e.getMessage())));
        }

        return inquiryAccountResponse;
    }

    private TransferResponse processExternalTransfer(InquiryAccountDataDTO inquiryAccountDataDTO, PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
        log.info("[External Placement] Inquiry Account: {}, Placement: {}, Error Messages: {}", inquiryAccountDataDTO, placementApproval, errorMessageDTOList);

        String placementTransferType = placementApproval.getPlacementTransferType();
        TransferResponse transferResponse = new TransferResponse();

        try {
            if (BI_FAST.equalsIgnoreCase(placementTransferType)) {
                CreditTransferRequest request = createCreditTransferBiFastRequest(inquiryAccountDataDTO, placementApproval);

                saveNCBSRequest(placementApproval, convertObjectToJson(request));

                CreditTransferResponse response = ncbsRequestService.creditTransfer(request);

                transferResponse.setResponseCode(response.getResponseCode());
                transferResponse.setResponseMessage(response.getResponseMessage());
                transferResponse.setTransferType(BI_FAST);
                transferResponse.setResponseJson(objectMapper.writeValueAsString(response));

            } else if (SKN.equalsIgnoreCase(placementTransferType) || RTGS.equalsIgnoreCase(placementTransferType)) {
                TransferSknRtgsRequest request = createTransferSknRtgsRequest(inquiryAccountDataDTO, placementApproval);

                saveNCBSRequest(placementApproval, convertObjectToJson(request));

                TransferSknRtgsResponse response = ncbsRequestService.transferSknRtgs(request);

                transferResponse.setResponseCode(response.getResponseCode());
                transferResponse.setResponseMessage(response.getResponseMessage());
                transferResponse.setTransferType(SKN + "-" + RTGS);
                transferResponse.setResponseJson(objectMapper.writeValueAsString(response));
            } else {
                log.warn("Unsupported placementTransferType: {}", placementTransferType);
                transferResponse.setResponseCode("INVALID_TYPE");
                transferResponse.setResponseMessage("Unsupported transfer type: " + placementTransferType);
            }

            boolean isSuccess = "200R000000".equals(transferResponse.getResponseCode());

            saveNCBSResponse(placementApproval, transferResponse.getResponseCode(), transferResponse.getResponseMessage(), transferResponse.getResponseJson(), isSuccess);

        } catch (Exception e) {
            log.error("Error processing external transfer for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
            errorMessageDTOList.add(
                    new ErrorMessageDTO("SYSTEM_ERROR", Collections.singletonList("Failed to process external transfer: " + e.getMessage()))
            );
        }
        return transferResponse;
    }

    private int processInternalPlacement(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
        log.info("[Internal Placement] Placement: {}, Error Messages: {}", placementApproval, errorMessageDTOList);
        // **1: Create overbooking casa request
        OverbookingCasaRequest request = createOverbookingCasaRequest(placementApproval);
        // **2: Save request object to the table NCBS Request
        saveNCBSRequest(placementApproval, convertObjectToJson(request));
        // **3: Send request to NCBS API
        OverbookingCasaResponse response = ncbsRequestService.overbookingCasa(request);

        // **4: Check response code
        boolean isSuccess = RESPONSE_CODE_SUCCESS.equals(response.getResponseCode());

        if (!isSuccess) {
            errorMessageDTOList.add(
                    ErrorMessageUtil.getOverbookingCasaErrorMessage(
                            response.getResponseCode(),
                            response.getResponseMessage()
                    )
            );
        }

        // **5:  Save response object to the table NCBS Response
        String responseJson = convertObjectToJson(response);
        saveNCBSResponse(placementApproval, response.getResponseCode(), response.getResponseMessage(), responseJson, isSuccess);
        return isSuccess ? 1 : 0;
    }

    private PlacementApprovalResponse buildPlacementApprovalResponse(int totalSuccess, int totalFailed, List<ErrorMessageDTO> errorMessageDTOList) {
        return PlacementApprovalResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }

    private String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error serializing object to JSON: {}", e.getMessage(), e);
            return "{\"error\": \"Failed to serialize object\"}";
        }
    }

    private void saveNCBSRequest(PlacementApproval placementApproval, String requestJson) {
        try {
            CreateNCBSRequest createNCBSRequest = CreateNCBSRequest.builder()
                    .createdDate(LocalDateTime.now())
                    .placementId(placementApproval.getId().toString())
                    .placementType(placementApproval.getPlacementType())
                    .placementProcessType(placementApproval.getPlacementProcessType())
                    .placementTransferType(placementApproval.getPlacementTransferType())
                    .requestJson(requestJson)
                    .build();

            NCBSRequest ncbsRequest = ncbsRequestService.saveToDatabase(createNCBSRequest);
            log.info("[NCBS Request] Successfully saved with ID: {}", ncbsRequest.getId());
        } catch (Exception e) {
            log.error("[NCBS Request] Failed to save placement for id {}: {}", placementApproval.getId(), e.getMessage(), e);
        }
    }

    private void saveNCBSResponse(PlacementApproval placementApproval, String responseCode, String responseMessage, String responseJson, boolean isSuccess) {
        try {
            CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
                    .createdDate(LocalDateTime.now())
                    .placementId(placementApproval.getId())
                    .placementType(placementApproval.getPlacementType())
                    .placementProcessType(placementApproval.getPlacementProcessType())
                    .placementTransferType(placementApproval.getPlacementTransferType())
                    .responseCode(responseCode)
                    .responseMessage(responseMessage)
                    .responseJson(responseJson)
                    .status(isSuccess ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED)
                    .build();

            NCBSResponse ncbsResponse = ncbsResponseService.saveToDatabase(createNCBSResponse);
            log.info("[Middleware Response] Successfully saved with ID: {}", ncbsResponse.getId());
        } catch (Exception e) {
            log.error("[Middleware Response] Failed to save placement for id {}: {}", placementApproval.getId(), e.getMessage(), e);
        }
    }

    private InquiryAccountRequest createInquiryAccountRequest(PlacementApproval placementApproval) {
        return InquiryAccountRequest.builder()
                .sttlmAmt(placementApproval.getPrinciple().toPlainString())
                .sttlmCcy("IDR")
                .chargeBearerCode("DEBT")
                .senderBic("BDINIDJA")
                .senderAcctNo(placementApproval.getAccountDebitNo())
                .benefBic(placementApproval.getBiCode())
                .benefAcctNo(placementApproval.getPlacementBankCashAccountNo())
                .purposeTransaction("51002")
                .build();
    }

    private OverbookingCasaRequest createOverbookingCasaRequest(PlacementApproval placementApproval) {
        return OverbookingCasaRequest.builder()
                .header(Collections.singletonList(
                        HeaderDTO.builder()
                                .codOrgBrn("0011")
                                .build())
                )
                .body(
                        BodyDTO.builder()
                                .acctIdFrom(
                                        AcctIdFromDTO.builder()
                                                .aTMCardNo("")
                                                .acctIdF("")
                                                .acctTypeF("")
                                                .costCtrF("9207")
                                                .build()
                                )
                                .acctIdTo(
                                        AcctIdToDTO.builder()
                                                .acctIdT(placementApproval.getAccountDebitNo())
                                                .acctTypeT("") // kalau CASA itu 20
                                                .costCtrT("")
                                                .build()
                                )
                                .xferInfo(
                                        XferInfoDTO.builder()
                                                .xferAmt("dari placement approval") // dari placement approval
                                                .xferdesc1("") // nama reksadana
                                                .xferdesc2("") // nama reksadana juga
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private TransferSknRtgsRequest createTransferSknRtgsRequest(InquiryAccountDataDTO inquiryAccountDataDTO, PlacementApproval placementApproval) {
        return TransferSknRtgsRequest.builder()
                .xferInfoFrom(
                        XferInfoFromDTO.builder()
                                .acctId("")
                                .acctType("") // casa
                                .acctCur("360")
                                .bdiAcctStatus("Y")
                                .bdiAcctCitizen("Y")
                                .build()
                )
                .bdiXferXFOff(
                        BdiXferXFOffDTO.builder()
                                .bdiXferAmtFrm("")
                                .bdiXferAmtFrmLCE(placementApproval.getPrinciple().toPlainString())
                                .bdiXferAmtTo("")
                                .bdiXferAmtToLCE("")
                                .bdiXferType("") // RTGS: ? SKN: ?
                                .bdiXferCurCode("")
                                .bdiXRateAmt("")
                                .bdiStdRateAmt("")
                                .bdiXReffNumber(placementApproval.getSiReferenceID())
                                .bdiXferBeneficiery(
                                        BdiXferBeneficieryDTO.builder()
                                                .bdiBenfID("")
                                                .bdiBenfAcct(inquiryAccountDataDTO.getBenefAcctNo())
                                                .bdiBenfName("")
                                                .bdiBenfAddress("")
                                                .bdiBenStatus("")
                                                .bdiBenCitizen("")
                                                .bankInfo(
                                                        BankInfoDTO.builder()
                                                                .biCode(placementApproval.getBiCode())
                                                                .cocCode("")
                                                                .name("")
                                                                .build()
                                                )
                                                .build()
                                )
                                .bdiXferCostCtr("9207")
                                .bdiFeeAmt("") // SKN 2.900, RTGS 30.000
                                .bdiFeeAmtLCE("")
                                .bdiFeeProcIr("")
                                .bdiXferMemo(
                                        BdiXferMemoDTO.builder()
                                                .bdiFrMemo1("")
                                                .bdiFrMemo2("")
                                                .bdiToMemo1("")
                                                .bdiToMemo2("")
                                                .build()
                                )
                                .transInfo(
                                        TransInfoDTO.builder()
                                                .trn("IFT00000")
                                                .fee("")
                                                .build()
                                )
                                .lldInfo("")
                                .build()
                )
                .build();
    }

    private CreditTransferRequest createCreditTransferBiFastRequest(InquiryAccountDataDTO inquiryAccountDataDTO, PlacementApproval placementApproval) {
        log.info("[Credit Transfer] Placement approval: {}", placementApproval);
        return CreditTransferRequest.builder()
                .trxType("CREDIT_TRANSFER")
                .category("01")
                .sttlmAmt(placementApproval.getPrinciple().toPlainString())
                .sttlmCcy("IDR")
                .sttlmDate(placementApproval.getPlacementDate().toString()) // format must be yyyy-MM-dd
                .feeAmt("2500.00") // fee BiFast
                .chargeBearerCode("DEBT")
                .senderAcctNo(placementApproval.getAccountDebitNo())
                .senderAcctType("SVGS")
                .senderBic("BDINIDJA")
                .benefBic(inquiryAccountDataDTO.getBenefId())
                .benefName(inquiryAccountDataDTO.getBenefAcctName())
                .benefId(inquiryAccountDataDTO.getBenefId())
                .benefAcctNo(inquiryAccountDataDTO.getBenefAcctNo())
                .benefAcctType(inquiryAccountDataDTO.getBenefAcctType())
                .proxyType("")
                .proxyValue("")
                .description("Payment for housing")
                .benefType(inquiryAccountDataDTO.getBenefType())
                .benefResidentStatus(inquiryAccountDataDTO.getBenefResidentStatus())
                .benefCityCode(inquiryAccountDataDTO.getBenefCityCode())
                .purposeTransaction("01")
                .cardNo("")
                .build();
    }

}
