package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.client.utils.MediaTypes;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.problems.PutProblemSolutionDTO;
import com.google.common.io.ByteStreams;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class Solver extends GatewayClient<AccessState> {

    protected Solver(URL gatewayUrl, AccessState state)
            throws IllegalAccessException, InstantiationException {

        super(gatewayUrl, state, AccessState.class);
    }

    public void putSolution(PutProblemSolutionDTO putProblemSolutionDTO)
            throws IOException, RequestException {

        byte[] boundary = new byte[24];
        new Random().nextBytes(boundary);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("SolutionStatus", putProblemSolutionDTO.SolutionStatus.toString())
                .addFormDataPart("Solution", null, new RequestBody() {

                    @Override
                    public MediaType contentType() {
                        return MediaTypes.OctetStream;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                        try (InputStream solutionStream = putProblemSolutionDTO.Solution.open()) {
                            ByteStreams.copy(solutionStream, sink.outputStream());
                        }
                    }
                })
                .build();
        Request request = createRequestBuilder(Routes.ProblemBaseUrl)
                .put(body)
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }
}
