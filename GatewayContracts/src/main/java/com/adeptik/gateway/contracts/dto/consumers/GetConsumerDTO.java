package com.adeptik.gateway.contracts.dto.consumers;

/**
 * Получение данных о потребителе
 */
@SuppressWarnings("unused")
public class GetConsumerDTO {

    /**
     * Уникальный идентификатор потребителя
     */
    public long Id;

    /**
     * Описание потребителя
     */
    public String Description;

    /**
     * Признак того, что авторизация потребителя отозвана
     */
    public boolean AuthorizationRevoked;
}
