package com.infy.scm360.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateRequisitionRequest(
        String reason,
        @NotEmpty @Valid List<RequisitionLineDto> lines
) {}
