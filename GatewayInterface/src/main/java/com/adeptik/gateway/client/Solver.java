package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.problems.PutProblemSolutionDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

/**
 * Клиент Шлюза от имени решателя Задачи
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Solver extends GatewayClient<AccessState> {

    /**
     * Создание экземпляра класса {@link Solver}
     *
     * @param gatewayUrl   Адрес Шлюза
     * @param state        Состояние клиента Шлюза
     * @param stateHandler Обработчик изменения состояния клиента Шлюза
     */
    protected Solver(URL gatewayUrl, AccessState state, StateHandler<AccessState> stateHandler) {

        super(gatewayUrl, state, stateHandler, AccessState.class);
    }

    /**
     * Отправка решения Задачи
     *
     * @param putProblemSolutionDTO Данные решения Задачи
     * @throws IOException            Ошибка ввода-вывода
     * @throws IllegalAccessException Не удается получить доступ к члену класса
     * @throws RequestException       Ошибка при выполнении HTTP-запроса
     */
    public void putSolution(PutProblemSolutionDTO putProblemSolutionDTO)
            throws IOException, RequestException, IllegalAccessException {

        Request request = createRequestBuilder(Routes.ProblemBaseUrl)
                .put(createFormRequestBody(putProblemSolutionDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }
}
