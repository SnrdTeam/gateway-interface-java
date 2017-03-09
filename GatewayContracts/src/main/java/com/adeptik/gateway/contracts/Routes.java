package com.adeptik.gateway.contracts;

/**
 * Маршруты контроллеров API
 */
public final class Routes {

    private static final String ApiUrl = "/api";

    /**
     * Контроллер для работы с запросами агентов на присоединение к шлюзу
     */
    public static final String AgentEnrollmentBaseUrl = ApiUrl + "/agentenrollment";

    /**
     * Контроллер для работы с агентами
     */
    public static final String AgentBaseUrl = ApiUrl + "/agent";

    /**
     * Контроллер для работы с пользователями
     */
    public static final String UserBaseUrl = ApiUrl + "/user";

    /**
     * Контроллер для работы с потреьителями
     */
    public static final String ConsumerBaseUrl = ApiUrl + "/consumer";

    /**
     * Контроллер для работы с алгоритмами
     */
    public static final String AlgorithmBaseUrl = ApiUrl + "/algorithm";

    /**
     * Контроллер для работы с задачами
     */
    public static final String ProblemBaseUrl = ApiUrl + "/problem";
}