package com.adeptik.gateway.contracts.dto.errors;

/**
 * Коды ошибок, связанные с алгоритмами
 */
public final class AlgorithmErrorCodes {

    /**
     * Добавляемый алгоритм уже существует (алгоритм с таким же хэшем)
     */
    public static final int AlgorithmAlreadyExists = 1;

    /**
     * Определение алгоритма имеет некорректный формат
     */
    public static final int InvalidAlgorithmDefinition = 2;
}
