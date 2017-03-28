package com.adeptik.gateway.contracts.dto.enrollment;

import com.adeptik.gateway.contracts.model.EnrollmentStatus;

/**
 * Данные запроса агента на присоединение к шлюзу
 */
@SuppressWarnings("unused")
public class GetAgentEnrollmentDTO {

    /**
     * Уникальные идентификатор запроса агента на присоединение к шлюзу
     */
    public long Id;

    /// <summary>
    /// Имя потенциального агента
    /// </summary>
    public String AgentName;

    /// <summary>
    /// Комментарий к запросу агента на присоединение к шлюзу
    /// </summary>
    public String Comment;

    /// <summary>
    /// Статус запроса агента на присоединение к шлюзу
    /// </summary>
    public EnrollmentStatus Status;
}
