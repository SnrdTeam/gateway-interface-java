package com.adeptik.gateway.contracts.model;

/**
 * Состояние установки алгоритма на агенте
 */
public enum AlgorithmState {

    /**
     * Алгоритм успешно установлен и готов к запуску
     */
    Installed,

    /**
     * Алгоритм несовместим со средами исполнения агента и не может быть запущен
     */
    Incompatible
}