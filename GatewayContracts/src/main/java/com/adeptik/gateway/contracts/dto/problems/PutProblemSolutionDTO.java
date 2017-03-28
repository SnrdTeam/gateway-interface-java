package com.adeptik.gateway.contracts.dto.problems;

import com.adeptik.gateway.contracts.dto.FormFile;
import com.adeptik.gateway.contracts.model.SolutionStatus;

/**
 * Решение задачи
 */
@SuppressWarnings("unused")
public class PutProblemSolutionDTO {

    /**
     * Решение задачи
     */
    public FormFile Solution;

    /**
     * Статус решения задачи
     */
    public SolutionStatus SolutionStatus;
}

