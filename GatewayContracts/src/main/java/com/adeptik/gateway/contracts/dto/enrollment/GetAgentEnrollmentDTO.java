package com.adeptik.gateway.contracts.dto.enrollment;

import com.adeptik.gateway.contracts.model.EnrollmentStatus;

public class GetAgentEnrollmentDTO {

    public long Id;

    /// <summary>
    /// Имя агента
    /// </summary>
    public String AgentName;

    /// <summary>
    /// Комментарий к запросу регистрации агента в шлюзе
    /// </summary>
    public String Comment;

    /// <summary>
    /// Статус запроса регистрации агента в шлюзе
    /// </summary>
    public EnrollmentStatus Status;
}
