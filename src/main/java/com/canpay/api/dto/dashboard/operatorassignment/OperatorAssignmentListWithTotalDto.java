package com.canpay.api.dto.dashboard.operatorassignment;

import java.util.List;

public class OperatorAssignmentListWithTotalDto {

    private long total;
    private List<OperatorAssignmentListResponseDto> assignments;

    public OperatorAssignmentListWithTotalDto(long total, List<OperatorAssignmentListResponseDto> assignments) {
        this.total = total;
        this.assignments = assignments;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<OperatorAssignmentListResponseDto> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<OperatorAssignmentListResponseDto> assignments) {
        this.assignments = assignments;
    }

}
