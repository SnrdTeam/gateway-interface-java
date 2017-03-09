package com.adeptik.gateway.contracts.model;

/**
 * Статус запроса агента на регистрацию в шлюзе
 */
public enum EnrollmentStatus {

    /**
     * Запрос на рассмотрении
     */
    Pending,

    /**
     * Доступ к шлюзу запрещен
     */
    Deny,

    /**
     * Доступ к шлюзу разрешен
     */
    Allow
}
