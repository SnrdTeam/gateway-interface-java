package com.adeptik.gateway.contracts.dto.agents;

import com.adeptik.gateway.contracts.model.NotificationMode;

/**
 * Данные для регистрации агента в шлюзе
 */
public class PostAgentInputDTO {

    /**
     * Период обновления состояния агента в милисекундах
     */
    public long AgentHeartbeatPeriod;

    /**
     * Способ оповещения об изменениях
     */
    public NotificationMode NotificationMode;
}
