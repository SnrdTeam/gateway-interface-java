package com.adeptik.gateway.contracts.dto.agents;

import com.adeptik.gateway.contracts.model.NotificationMode;

import java.net.URL;

/**
 * Настройки оповещения Агента об изменениях
 */
public class AgentNotificationSettingsDTO {

    /**
     * Способ оповещения об изменениях
     */
    public NotificationMode NotificationMode;

    /**
     * В режиме уведомлений  {@link NotificationMode#AgentAsServer} - адрес, по которому Шлюз отправляет запросы Агенту
     */
    public URL Url;

    /**
     * В режиме уведомлений {@link NotificationMode#AgentAsServer} - специальная последовательность символов, выданная агентом,
     * которая позволяет аутентифицироваться в агенте
     */
    public String Ticket;
}
