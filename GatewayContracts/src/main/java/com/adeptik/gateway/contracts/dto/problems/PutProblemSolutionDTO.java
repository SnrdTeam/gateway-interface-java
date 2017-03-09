package com.adeptik.gateway.contracts.dto.problems;

import com.adeptik.gateway.contracts.dto.FormFile;
import com.adeptik.gateway.contracts.model.SolutionStatus;

/**
 * Решение задачи
 */
public class PutProblemSolutionDTO {

    /**
     * Решение задачи
     */
    public FormFile Solution;

    /**
     * Является ли предоставленное решение окончательным (иначе - промежуточным)
     */
    public SolutionStatus SolutionStatus;
}

