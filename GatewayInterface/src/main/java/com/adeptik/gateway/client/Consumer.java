package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.utils.StreamHandler;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.algorithms.GetAlgorithmDTO;
import com.adeptik.gateway.contracts.dto.algorithms.PostAlgorithmInputDTO;
import com.adeptik.gateway.contracts.dto.algorithms.PostAlgorithmOutputDTO;
import com.adeptik.gateway.contracts.dto.problems.GetProblemDTO;
import com.adeptik.gateway.contracts.dto.problems.PostProblemInputDTO;
import com.adeptik.gateway.contracts.dto.problems.PostProblemOutputDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

/**
 * Клиент Шлюза от имени Потребителя
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Consumer extends AccessRefreshGatewayClient {

    /**
     * Создание экземпляра класса {@link Consumer}
     *
     * @param gatewayUrl   Адрес Шлюза
     * @param state        Состояние клиента Шлюза
     * @param stateHandler Обработчик изменения состояния клиента Шлюза
     */
    protected Consumer(URL gatewayUrl, AccessServiceState state, StateHandler stateHandler) {

        super(gatewayUrl, state, Routes.ConsumerBaseUrl + "/token", "x-consumer", "x-consumer-service", stateHandler);
    }

    /**
     * Добавление Алгоритма в Шлюз
     *
     * @param postAlgorithmInputDTO Данные Алгоритма
     * @return Данные о добавленном Алгоритме
     * @throws IOException            Ошибка ввода-вывода
     * @throws IllegalAccessException Не удается получить доступ к члену класса
     * @throws RequestException       Ошибка при выполнении HTTP-запроса
     */
    public PostAlgorithmOutputDTO postAlgorithm(PostAlgorithmInputDTO postAlgorithmInputDTO)
            throws IOException, IllegalAccessException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AlgorithmBaseUrl)
                .post(createFormRequestBody(postAlgorithmInputDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, PostAlgorithmOutputDTO.class);
        }
    }

    /**
     * Удаление Алгоритма из Шлюза
     *
     * @param algorithmId Уникальный идентификатор Алгоритма
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void deleteAlgorithm(long algorithmId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AlgorithmBaseUrl + "/" + algorithmId)
                .delete()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Получение данных об Алгоритмах из Шлюза
     *
     * @return Коллекция Алгоритмов
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public Iterable<GetAlgorithmDTO> getAlgorithms()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AlgorithmBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    /**
     * Добавление Задачи в Шлюз
     *
     * @param postProblemInputDTO Данные Задачи
     * @return Данные о добавленной Задаче
     * @throws IOException            Ошибка ввода-вывода
     * @throws IllegalAccessException Не удается получить доступ к члену класса
     * @throws RequestException       Ошибка при выполнении HTTP-запроса
     */
    public PostProblemOutputDTO postProblem(PostProblemInputDTO postProblemInputDTO)
            throws IOException, IllegalAccessException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl)
                .post(createFormRequestBody(postProblemInputDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, PostProblemOutputDTO.class);
        }
    }

    /**
     * Получение данных о Задаче
     *
     * @param problemId Уникальный идентификатор Задачи
     * @return Данные о Задаче
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public GetProblemDTO getProblem(long problemId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl + "/" + problemId)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, GetProblemDTO.class);
        }
    }

    /**
     * Получение решения Задачи
     *
     * @param problemId     Уникальный идентификатор Задачи
     * @param resultHandler Обработчик получения решения Задачи
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void getProblemSolution(long problemId, StreamHandler resultHandler)
            throws IOException, RequestException {

        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl + "/" + problemId + "/solution")
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            readFileResponse(response, resultHandler);
        }
    }
}
