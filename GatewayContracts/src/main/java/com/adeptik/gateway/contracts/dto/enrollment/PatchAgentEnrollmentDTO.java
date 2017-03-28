package com.adeptik.gateway.contracts.dto.enrollment;

import com.adeptik.gateway.contracts.model.EnrollmentStatus;

/**
 * Обновление данных запроса агента на присоединение к шлюзу
 */
public class PatchAgentEnrollmentDTO {

    /**
     * Статус запроса агента на присоединение к шлюзу
     */
    public EnrollmentStatus Status;
}
