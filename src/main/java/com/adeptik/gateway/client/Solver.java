package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.problems.PutProblemSolutionDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Solver extends GatewayClient<AccessState> {

    protected Solver(URL gatewayUrl, AccessState state)
            throws IllegalAccessException, InstantiationException {

        super(gatewayUrl, state, AccessState.class);
    }

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
