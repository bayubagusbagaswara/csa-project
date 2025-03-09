package com.services.billingservice.controller.placement;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.placement.depositapproval.DepositApprovalRequest;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalDTO;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalResponse;
import com.services.billingservice.service.placement.PlacementApprovalService;
import com.services.billingservice.utils.ClientIPUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/api/placement/placement-approval")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequiredArgsConstructor
public class PlacementApprovalController {

    private final PlacementApprovalService depositApprovalService;

    @PostMapping(path = "/approve")
    public ResponseEntity<ResponseDTO<PlacementApprovalResponse>> approve(@RequestBody DepositApprovalRequest approvalRequest, HttpServletRequest servletRequest) {
        String approveIPAddress = ClientIPUtil.getClientIp(servletRequest);
        String approveId = approvalRequest.getApproverId();
        List<Long> placementApprovalIds = approvalRequest.getPlacementApprovalIds();

        PlacementApprovalResponse approve = depositApprovalService.approve(placementApprovalIds, approveId, approveIPAddress);

        ResponseDTO<PlacementApprovalResponse> response = ResponseDTO.<PlacementApprovalResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(approve)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/reject")
    public ResponseEntity<ResponseDTO<PlacementApprovalResponse>> reject(@RequestBody DepositApprovalRequest approvalRequest, HttpServletRequest servletRequest) {
        String approveIPAddress = ClientIPUtil.getClientIp(servletRequest);
        String approveId = approvalRequest.getApproverId();
        List<Long> placementApprovalIds = approvalRequest.getPlacementApprovalIds();

        PlacementApprovalResponse approveReject = depositApprovalService.reject(placementApprovalIds, approveId, approveIPAddress);

        ResponseDTO<PlacementApprovalResponse> response = ResponseDTO.<PlacementApprovalResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(approveReject)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/current-date/approval-status")
    public ResponseEntity<ResponseDTO<List<PlacementApprovalDTO>>> getAllByCurrentDateAndApprovalStatus(@RequestParam("approvalStatus") String approvalStatus) {
        List<PlacementApprovalDTO> placementApprovalDTOList = depositApprovalService.getAllByCurrentDateAndApprovalStatus(approvalStatus);
        ResponseDTO<List<PlacementApprovalDTO>> response = ResponseDTO.<List<PlacementApprovalDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(placementApprovalDTOList)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/current-date")
    public ResponseEntity<ResponseDTO<List<PlacementApprovalDTO>>> getAllByDateAndStatusIsPending() {
        List<PlacementApprovalDTO> placementApprovalDTOList = depositApprovalService.getAllByDateAndApprovalStatusIsPending();
        ResponseDTO<List<PlacementApprovalDTO>> response = ResponseDTO.<List<PlacementApprovalDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(placementApprovalDTOList)
                .build();
        return ResponseEntity.ok(response);
    }

}
