package com.services.billingservice.service.placement;

import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalDTO;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalResponse;
import com.services.billingservice.model.placement.PlacementApproval;

import java.util.List;

public interface PlacementApprovalService {

    PlacementApprovalResponse approve(List<Long> placementApprovalIds, String approveId, String approveIPAddress);

    PlacementApprovalResponse reject(List<Long> placementApprovalIds, String approveId, String approveIPAddress);

    List<PlacementApprovalDTO> getAllByCurrentDateAndApprovalStatus(String approvalStatus);

    List<PlacementApprovalDTO> getAllByDateAndApprovalStatusIsPending();

    PlacementApproval getById(Long id);

}