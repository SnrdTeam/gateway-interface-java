package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.agents.GetAgentDTO;
import com.adeptik.gateway.contracts.dto.consumers.GetConsumerDTO;
import com.adeptik.gateway.contracts.dto.consumers.PatchConsumerDTO;
import com.adeptik.gateway.contracts.dto.consumers.PostConsumerInputDTO;
import com.adeptik.gateway.contracts.dto.enrollment.AgentEnrollmentSettingsDTO;
import com.adeptik.gateway.contracts.dto.enrollment.GetAgentEnrollmentDTO;
import com.adeptik.gateway.contracts.dto.enrollment.PatchAgentEnrollmentDTO;
import com.adeptik.gateway.contracts.dto.security.AccessServiceTokensDTO;
import com.adeptik.gateway.contracts.dto.users.GetUserDTO;
import com.adeptik.gateway.contracts.dto.users.PatchUserDTO;
import com.adeptik.gateway.contracts.dto.users.SignInInputDTO;
import com.adeptik.gateway.contracts.dto.users.UserRegisterDTO;
import com.google.common.primitives.Bytes;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Клиент Шлюза от имени Пользователя
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class User extends AccessRefreshGatewayClient {

    /**
     * Создание экземпляра класса {@link User}
     *
     * @param gatewayUrl Адрес Шлюза
     * @param state      Состояние клиента Шлюза
     */
    protected User(URL gatewayUrl, AccessServiceState state) {

        super(gatewayUrl, state, Routes.UserBaseUrl + "/token", "x-user", "x-user-service");
    }

    /**
     * Создание экземпляра класса {@link User}
     *
     * @param gatewayUrl Адрес Шлюза
     * @param userName   Имя Пользователя для авторизации в Шлюзе
     * @param password   Пароль Пользователя для авторизации в Шлюзе
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    protected User(URL gatewayUrl, String userName, String password)
            throws IOException, RequestException {

        this(gatewayUrl, new AccessServiceState());
        signIn(userName, password);
    }

    /**
     * Авторизация Пользователя для данного экземпляра клиента Шлюза от имени Пользователя
     *
     * @param userName Имя Пользователя для авторизации в Шлюзе
     * @param password Пароль Пользователя для авторизации в Шлюзе
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    private void signIn(String userName, String password)
            throws IOException, RequestException {

        long now = now();

        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
        byte[] passwordHash = sha256.digest(password.getBytes("UTF-8"));
        byte[] salt = new byte[16];
        new Random().nextBytes(salt);
        byte[] hashedPassword = sha256.digest(Bytes.concat(passwordHash, salt));

        Request request = createRequestBuilder(Routes.UserBaseUrl + "/signin")
                .post(createJsonRequestBody(new SignInInputDTO() {
                    {
                        this.UserName = userName;
                        this.HashedPassword = hashedPassword;
                        this.Salt = salt;
                    }
                }, SignInInputDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            AccessServiceTokensDTO tokens = readJsonResponse(response, AccessServiceTokensDTO.class);
            _state.setAccessToken(tokens.Access.Token);
            _state.setAccessTokenValidTo(now + tokens.Access.ExpiresIn);
            _state.setServiceToken(tokens.Service.Token);
            _state.setServiceTokenValidTo(now + tokens.Service.ExpiresIn);
        }

        onStateChanged();
    }

    /**
     * Регистрация нового Пользователя
     *
     * @param userRegisterDTO Данные регистрируемого Пользователя
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void postUser(UserRegisterDTO userRegisterDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl)
                .post(createJsonRequestBody(userRegisterDTO, UserRegisterDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Обновление данных Пользователя
     *
     * @param userName     Имя обновляемого Пользователя
     * @param patchUserDTO Обновляемые данные Пользователя
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void patchUser(String userName, PatchUserDTO patchUserDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl + "/" + userName)
                .patch(createJsonRequestBody(patchUserDTO, PatchUserDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Получение данных о всех Пользователях
     *
     * @return Данные о всех Пользователях
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public Iterable<GetUserDTO> getUsers()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    /**
     * Удаление Пользователя
     *
     * @param userName Имя удаляемого Пользователя
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void deleteUser(String userName)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl + "/" + userName)
                .delete()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Получение настроек Шлюза в отношении присоединения Агентов
     *
     * @return Настройки Шлюза в отношении присоединения Агентов
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public AgentEnrollmentSettingsDTO getAgentEnrollmentSettings()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/settings")
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AgentEnrollmentSettingsDTO.class);
        }
    }

    /**
     * Обновление настроек Шлюза в отношении присоединения Агентов
     *
     * @param agentEnrollmentSettingsDTO Настройки Шлюза в отношении присоединения Агентов
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void putAgentEnrollmentSettings(AgentEnrollmentSettingsDTO agentEnrollmentSettingsDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/settings")
                .put(createJsonRequestBody(agentEnrollmentSettingsDTO, AgentEnrollmentSettingsDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Получение данных Агентов на присоединение к Шлюзу
     *
     * @return Данные Агентов на присоединение к Шлюзу
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public Iterable<GetAgentEnrollmentDTO> getAgentEnrollments()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    /**
     * Обновление данных Агента на присоединение к Шлюзу
     *
     * @param agentEnrollmentId       Уникальный идентификатор запроса Агента на присоединение к Шлюзу
     * @param patchAgentEnrollmentDTO Обновляемые данные запроса Агента на присоединение к Шлюзу
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void patchAgentEnrollment(long agentEnrollmentId, PatchAgentEnrollmentDTO patchAgentEnrollmentDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/" + agentEnrollmentId)
                .patch(createJsonRequestBody(patchAgentEnrollmentDTO, PatchAgentEnrollmentDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Удаление данных Агента на присоединение к Шлюзу
     *
     * @param agentEnrollmentId Уникальный идентификатор запроса Агента на присоединение к Шлюзу
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void deleteAgentEnrollment(long agentEnrollmentId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/" + agentEnrollmentId)
                .delete()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    /**
     * Получение данных об Агентах, зарегистрированных в Шлюзе
     *
     * @return Данных об Агентах, зарегистрированных в Шлюзе
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public Iterable<GetAgentDTO> getAgents()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    /**
     * Получение данных о Потребителях, зарегистрированных в Шлюзе
     *
     * @return Данные о Потребителях, зарегистрированных в Шлюзе
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public Iterable<GetConsumerDTO> getConsumers()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ConsumerBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    /**
     * Добавление Потребителя
     *
     * @param postConsumerInputDTO Данные Потребителя
     * @return Токены для доступа Потребителя к Шлюзу
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public AccessServiceTokensDTO postConsumer(PostConsumerInputDTO postConsumerInputDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ConsumerBaseUrl)
                .post(createJsonRequestBody(postConsumerInputDTO, PostConsumerInputDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AccessServiceTokensDTO.class);
        }
    }

    /**
     * Обновление данных Потребителя
     *
     * @param consumerId       Уникальный идентификатор Потребителя
     * @param patchConsumerDTO Обновляемые данные Потребителя
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка при выполнеии HTTP-запроса
     */
    public void patchConsumer(long consumerId, PatchConsumerDTO patchConsumerDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ConsumerBaseUrl + "/" + consumerId)
                .patch(createJsonRequestBody(patchConsumerDTO, PatchConsumerDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }
}
