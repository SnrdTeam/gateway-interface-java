package com.adeptik.gateway.contracts.dto.consumers;

/**
 * Обновление данных потребителя
 */
@SuppressWarnings("unused")
public class PatchConsumerDTO {

    /**
     * Признак того, что авторизация потребителя отозвана
     */
    public Boolean AuthorizationRevoked;
}
