package au.org.raid.inttest.service;

import au.org.raid.idl.raidv2.api.RaidApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class TestClient {
    private final ObjectMapper objectMapper;
    private final Contract contract;
    @Value("${raid.test.api.url}")
    private String apiUrl;
    private final TokenService tokenService;

    public RaidApi raidApi(
            final String token
    ) {
        return Feign.builder()
                .options(
                        new Request.Options(10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, false)
                )

                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new ResponseEntityDecoder(new JacksonDecoder(objectMapper)))
                .errorDecoder(new RaidApiExceptionDecoder(objectMapper))
                .contract(contract)
                .requestInterceptor(request -> request.header(AUTHORIZATION, "Bearer " + token))
                .requestInterceptor(request -> request.header("X-Raid-Api-Version", "3"))
                .logger(new Slf4jLogger(RaidApi.class))
                .logLevel(Logger.Level.FULL)
                .target(RaidApi.class, apiUrl);
    }

    public RaidApi raidApi(final String username, final String password) {
        final var token = tokenService.getToken(username, password);
        return raidApi(token);
    }

}