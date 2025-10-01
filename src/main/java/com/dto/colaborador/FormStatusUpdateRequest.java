package com.dto.colaborador;

import com.domain.user.Enum.StatusForm;

public record FormStatusUpdateRequest(
        StatusForm status
) {}