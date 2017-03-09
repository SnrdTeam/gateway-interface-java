package com.adeptik.gateway.contracts.model;

/**
 * Способ оповещения об изменениях
 */
public enum NotificationMode {

    /**
     * Пассивный: агент сам периодически опрашивает шлюз
     */
    Passive,

    /**
     * Google Cloud Messaging: шлюз уведомляет агента с помощью сервиса Google Cloud Messaging.
     * Агент должен присылать свои идентификаторы для работы в Google Cloud Messaging
     */
    GCM,

    /**
     * Агент имеет собственный web-сервер, API которого известен шлюзу.
     * Агент должен присылать свой url.
     */
    AgentAsServer
}