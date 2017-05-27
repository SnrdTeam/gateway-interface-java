package com.adeptik.gateway.contracts.dto.agents;

/**
 * Данные для регистрации агента в шлюзе
 */
public class PostAgentInputDTO {

    /**
     * Период обновления состояния агента в милисекундах
     */
    public long AgentHeartbeatPeriod;

    /**
     * Настройки оповещения Агента об изменениях
     */
    public AgentNotificationSettingsDTO NotificationSettings;
}
