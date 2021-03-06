package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.agents.PostAgentInputDTO;
import com.adeptik.gateway.contracts.dto.enrollment.PostAgentEnrollmentInputDTO;
import com.adeptik.gateway.contracts.dto.security.AccessServiceTokensDTO;
import com.adeptik.gateway.contracts.dto.security.TokenDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

/**
 * Клиент Шлюза от имени потенциального Агента
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AgentEnrollment extends GatewayClient<AccessState> {

    /**
     * Создание экземпляра класса {@link AgentEnrollment}
     *
     * @param gatewayUrl   Адрес Шлюза
     * @param stateHandler Обработчик изменения состояния клиента Шлюза
     * @param accessState  Состояние клиента Шлюза
     */
    protected AgentEnrollment(URL gatewayUrl, AccessState accessState, StateHandler stateHandler) {

        super(gatewayUrl, accessState, stateHandler, AccessState.class);
    }

    /**
     * Отправка запроса Агента на присоединение к Шлюзу
     *
     * @param postAgentEnrollmentInputDTO Данные запроса Агента на присоединение к Шлюзу
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void postEnrollment(PostAgentEnrollmentInputDTO postAgentEnrollmentInputDTO)
            throws IOException, RequestException {

        long now = now();

        Request request = createRequestBuilder(Routes.AgentEnrollmentBaseUrl)
                .post(createJsonRequestBody(postAgentEnrollmentInputDTO, PostAgentEnrollmentInputDTO.class))
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            TokenDTO token = readJsonResponse(response, TokenDTO.class);
            _state.setAccessToken(token.Token);
            _state.setAccessTokenValidTo(now + token.ExpiresIn);
        }

        onStateChanged();
    }

    /**
     * Завершение регистрации Агента в Шлюзе
     *
     * @param postAgentInputDTO Данные для регистрации агента в шлюзе
     * @return Токены для доступа Агента к Шлюзу
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public AccessServiceTokensDTO enroll(PostAgentInputDTO postAgentInputDTO)
            throws IOException, RequestException {

        long now = now();

        Request request = createRequestBuilder(Routes.AgentBaseUrl)
                .addHeader("Authorization", "x-agent-enroll " + _state.getAccessToken())
                .post(createJsonRequestBody(postAgentInputDTO, PostAgentInputDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AccessServiceTokensDTO.class);
        }
    }
}
