package com.adeptik.gateway.contracts.model;

/**
 * Состояние назначения задачи агенту
 */
public enum ProblemAssignmentState {

    /**
     * Задача назначена
     */
    Assigned(101),

    /**
     * Задача принята агентом для решения
     */
    Accepted(102),

    /**
     * Запущен поиск решения задачи
     */
    Running(ProblemAssignmentExecutionState.Running.Value),

    /**
     * Алгоритм успешно завершил выполнение
     */
    Completed(ProblemAssignmentExecutionState.Completed.Value),

    /**
     * Алгоритм завершил выполнения с ошибкой
     */
    Failed(ProblemAssignmentExecutionState.Failed.Value),

    /**
     * Выполнение алгоритма успешно прервано агентом
     */
    Interrupted(ProblemAssignmentExecutionState.Interrupted.Value);

    /**
     * Соответствующее числовое значение
     */
    public final int Value;

    ProblemAssignmentState(int value) {

        Value = value;
    }
}
