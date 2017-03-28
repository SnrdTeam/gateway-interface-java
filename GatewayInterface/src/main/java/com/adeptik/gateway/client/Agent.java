package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.utils.StreamHandler;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.agents.AgentJobDTO;
import com.adeptik.gateway.contracts.dto.agents.PatchAssignmentDTO;
import com.adeptik.gateway.contracts.dto.agents.PostAgentStateInputDTO;
import com.adeptik.gateway.contracts.dto.agents.PutAlgorithmStateDTO;
import com.adeptik.gateway.contracts.dto.security.TokenDTO;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

/**
 * Клиент Шлюза от имени Агента
 */
@SuppressWarnings("unused")
public class Agent extends AccessRefreshGatewayClient {

    /**
     * Создание экземпляра класса {@link Agent}
     *
     * @param gatewayUrl Адрес Шлюза
     * @param state      Состояние клиента Шлюза
     */
    Agent(URL gatewayUrl, AccessServiceState state) {

        super(
                gatewayUrl,
                state,
                Routes.AgentBaseUrl + "/token",
                "x-agent",
                "x-agent-service");
    }

    /**
     * Отправка состояния Агента
     *
     * @param postAgentStateInputDTO Данные о сосотоянии Агента
     * @return Задание для Агента
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public AgentJobDTO postState(PostAgentStateInputDTO postAgentStateInputDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/state")
                .post(createJsonRequestBody(postAgentStateInputDTO, PostAgentStateInputDTO.class))
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AgentJobDTO.class);
        }
    }

    /**
     * Принять назначенную для поиска решения Задачу
     *
     * @param problemId Уникальный идентификатор Задачи
     * @return Информация для авторизации решателя данной Задачи
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public TokenDTO acceptAssignment(long problemId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/problem/" + problemId + "/assignment/accept")
                .put(createEmptyRequestBody())
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, TokenDTO.class);
        }
    }

    /**
     * Обновление данных назначения Задачи Агенту
     *
     * @param problemId          Уникальный идентификатор Задачи
     * @param patchAssignmentDTO Данные для обновления назначения Задачи Агенту
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void patchAssignment(long problemId, PatchAssignmentDTO patchAssignmentDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/problem/" + problemId + "/assignment")
                .patch(createJsonRequestBody(patchAssignmentDTO, PatchAssignmentDTO.class))
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Получение Определения алгоритма
     *
     * @param algorithmId   Уникальный идентификатор Алгоритма
     * @param runtimes      Поддерживаемые Среды исполнения
     * @param resultHandler Обработчик получения Определения алгоритма
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void getAlgorithmDefinition(long algorithmId, String[] runtimes, StreamHandler resultHandler)
            throws IOException, RequestException {

        if (runtimes == null)
            throw new IllegalArgumentException("runtimes not specified");
        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        //noinspection Guava,Convert2Lambda
        String runtimesQuery = Joiner.on("&")
                .join(FluentIterable.of(runtimes)
                        .transform(new Function<String, String>() {

                            @Override
                            public String apply(String runtime) {
                                return "runtimes=" + runtime;
                            }
                        }));
        Request request = createAuthorizedRequestBuilder(
                Routes.AlgorithmBaseUrl + "/" + algorithmId + "/definition?" + runtimesQuery)
                .get()
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            readFileResponse(response, resultHandler);
        }
    }

    /**
     * Получение Определения задачи
     *
     * @param problemId     Уникальный идентификатор Задачи
     * @param resultHandler Обработчик получения Определения задачи
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void getProblemDefinition(long problemId, StreamHandler resultHandler)
            throws IOException, RequestException {

        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl + "/" + problemId + "/definition")
                .get()
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            readFileResponse(response, resultHandler);
        }
    }

    /**
     * Отправка состояния Определения алгоритма на Агенте
     *
     * @param algorithmId          Уникальный идентификатор Алгоритма
     * @param putAlgorithmStateDTO Данные о состоянии Определения алгоритма на Агенте
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void putAlgorithm(long algorithmId, PutAlgorithmStateDTO putAlgorithmStateDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/algorithm/" + algorithmId)
                .put(createJsonRequestBody(putAlgorithmStateDTO, PutAlgorithmStateDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }
}
