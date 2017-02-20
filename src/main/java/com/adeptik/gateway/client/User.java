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
import com.adeptik.gateway.contracts.dto.security.AccessRefreshTokensDTO;
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

public class User extends AccessRefreshGatewayClient {

    protected User(URL gatewayUrl, AccessServiceState state)
            throws InstantiationException, IllegalAccessException {

        super(gatewayUrl, state, Routes.UserBaseUrl + "/token", "x-user", "x-user-service");
    }

    protected User(URL gatewayUrl, String userName, String password)
            throws IllegalAccessException, InstantiationException, NoSuchAlgorithmException, IOException, RequestException {

        this(gatewayUrl, new AccessServiceState());
        signIn(userName, password);
    }

    private void signIn(String userName, String password)
            throws IOException, NoSuchAlgorithmException, RequestException {

        long now = now();

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
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

            AccessRefreshTokensDTO tokens = readJsonResponse(response, AccessRefreshTokensDTO.class);
            _state.setAccessToken(tokens.Access.Token);
            _state.setAccessTokenValidTo(now + tokens.Access.ExpiresIn);
            _state.setServiceToken(tokens.Service.Token);
            _state.setServiceTokenValidTo(now + tokens.Service.ExpiresIn);
        }
    }

    public void postUser(UserRegisterDTO userRegisterDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl)
                .post(createJsonRequestBody(userRegisterDTO, UserRegisterDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public void patchUser(String userName, PatchUserDTO patchUserDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl + "/" + userName)
                .patch(createJsonRequestBody(patchUserDTO, PatchUserDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public Iterable<GetUserDTO> getUsers()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    public void deleteUser(String userName)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.UserBaseUrl + "/" + userName)
                .delete()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public AgentEnrollmentSettingsDTO getAgentEnrollmentSettings()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/settings")
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AgentEnrollmentSettingsDTO.class);
        }
    }

    public void putAgentEnrollmentSettings(AgentEnrollmentSettingsDTO agentEnrollmentSettingsDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/settings")
                .put(createJsonRequestBody(agentEnrollmentSettingsDTO, AgentEnrollmentSettingsDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public Iterable<GetAgentEnrollmentDTO> getAgentEnrollments()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    public void patchAgentEnrollment(long agentEnrollmentId, PatchAgentEnrollmentDTO patchAgentEnrollmentDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/" + agentEnrollmentId)
                .patch(createJsonRequestBody(patchAgentEnrollmentDTO, PatchAgentEnrollmentDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public void deleteAgentEnrollment(long agentEnrollmentId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentEnrollmentBaseUrl + "/" + agentEnrollmentId)
                .delete()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public Iterable<GetAgentDTO> getAgents()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    public Iterable<GetConsumerDTO> getConsumers()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ConsumerBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    public AccessRefreshTokensDTO postConsumer(PostConsumerInputDTO postConsumerInputDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ConsumerBaseUrl)
                .post(createJsonRequestBody(postConsumerInputDTO, PostConsumerInputDTO.class))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AccessRefreshTokensDTO.class);
        }
    }

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
