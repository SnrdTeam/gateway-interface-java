package com.adeptik.gateway.contracts.dto;

/**
 * Детали ошибки
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ErrorDTO {

    /**
     * Создает экземпляр класса {@link ErrorDTO}
     *
     * @param errorCode Код ошибки
     */
    public ErrorDTO(int errorCode) {
        ErrorCode = errorCode;
    }

    /**
     * Код ошибки
     */
    public final int ErrorCode;
}
