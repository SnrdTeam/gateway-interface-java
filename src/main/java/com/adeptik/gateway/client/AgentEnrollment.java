package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.agents.PostAgentInputDTO;
import com.adeptik.gateway.contracts.dto.enrollment.PostAgentEnrollmentInputDTO;
import com.adeptik.gateway.contracts.dto.security.AccessRefreshTokensDTO;
import com.adeptik.gateway.contracts.dto.security.TokenDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public class AgentEnrollment extends GatewayClient<AccessState> {

    protected AgentEnrollment(URL gatewayUrl, AccessState accessState)
            throws IllegalAccessException, InstantiationException {

        super(gatewayUrl, accessState, AccessState.class);
    }

    public void postEnrollment(PostAgentEnrollmentInputDTO postAgentEnrollmentInputDTO)
            throws IOException, RequestException {

        long now = now();

        Request request = createRequestBuilder(Routes.AgentEnrollmentBaseUrl)
                .post(createJsonRequestBody(postAgentEnrollmentInputDTO))
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            TokenDTO token = readJsonResponse(response, TokenDTO.class);
            _state.setAccessToken(token.Token);
            _state.setAccessTokenValidTo(now + token.ExpiresIn);
        }
    }

    public AccessServiceState enroll(PostAgentInputDTO postAgentInputDTO)
            throws IOException, RequestException {

        long now = now();

        Request request = createRequestBuilder(Routes.AgentBaseUrl)
                .addHeader("Authorization", "x-agent-enroll " + _state.getAccessToken())
                .post(createJsonRequestBody(postAgentInputDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            AccessRefreshTokensDTO tokens = readJsonResponse(response, AccessRefreshTokensDTO.class);

            AccessServiceState result = new AccessServiceState();
            result.setAccessToken(tokens.Access.Token);
            result.setAccessTokenValidTo(now + tokens.Access.ExpiresIn);
            result.setServiceToken(tokens.Service.Token);
            result.setServiceTokenValidTo(now + tokens.Service.ExpiresIn);
            return result;
        }
    }
}
