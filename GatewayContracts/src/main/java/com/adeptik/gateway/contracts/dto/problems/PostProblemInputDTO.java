package com.adeptik.gateway.contracts.dto.problems;

import com.adeptik.gateway.contracts.dto.FormFile;

/**
 * Данные добавляемой задачи
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

    /**
     * Максимальное количество агентов, которые необходимо задействовать в решении задачи
     */
    public Integer MaxAssignments;
}

