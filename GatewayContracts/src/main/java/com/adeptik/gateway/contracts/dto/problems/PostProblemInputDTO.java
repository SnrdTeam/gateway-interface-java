package com.adeptik.gateway.contracts.dto.problems;

import com.adeptik.gateway.contracts.dto.FormFile;

/**
 * Данные новой задачи
 */
public class PostProblemInputDTO {

    /**
     * Идентифкатор используемого алгоритма
     */
    public Long AlgorithmId;

    /**
     * Определение задачи
     */
    public FormFile ProblemDefinition;

    /**
     * Максимальная продолжительность поиска решения в милисекундах
     */
    public Long MaxExecutionDuration;

    public Integer MaxAssignments;
}

