package com.adeptik.gateway.contracts.dto;

public class ErrorDTO {

    public ErrorDTO(int errorCode) {
        ErrorCode = errorCode;
    }

    public final int ErrorCode;
}
