package com.adeptik.gateway.contracts.dto.problems;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Информация о задаче
 */
public class GetProblemDTO {

    /**
     * Статус поиска решения задачи
     */
    public SolutionSearchStatus SolutionSearchStatus;

    /**
     * Идентификатор алгоритма, используемого для решения данной задачи
     */
    public long AlgorithmId;

    /**
     * Дата и время создания задачи
     */
    public LocalDateTime Created;

    /**
     * Максимальная продолжительность поиска решения в милисекундах
     */
    public long MaxExecutionDuration;

    /**
     * Максимально необходимое количество агентов
     */
    public int MaxAssignments;

    /**
     * Назначенные агенты
     */
    public Collection<GetProblemAssignmentDTO> Assignments;
}

