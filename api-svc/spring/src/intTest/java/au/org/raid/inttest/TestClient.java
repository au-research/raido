package au.org.raid.inttest;

import au.org.raid.idl.raidv1.api.RaidV1Api;
import au.org.raid.idl.raidv2.api.BasicRaidExperimentalApi;
import au.org.raid.idl.raidv2.api.RaidoStableV1Api;
import au.org.raid.inttest.config.IntTestProps;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.concurrent.TimeUnit;

import static au.org.raid.api.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class TestClient {
    private final ObjectMapper objectMapper;
    private final Contract contract;

    private final IntTestProps props;

    public TestClient(final ObjectMapper objectMapper, final Contract contract, final IntTestProps props) {
        this.objectMapper = objectMapper;
        this.contract = contract;
        this.props = props;
    }

    public BasicRaidExperimentalApi basicRaidExperimentalClient(
        final String token
    ) {
        return Feign.builder().
            client(new OkHttpClient()).
            encoder(new JacksonEncoder(objectMapper)).
            decoder(new JacksonDecoder(objectMapper)).
            contract(contract).
            requestInterceptor(request ->
                request.header(AUTHORIZATION, "Bearer " + token)).
            logger(new Slf4jLogger(BasicRaidExperimentalApi.class)).
            logLevel(Logger.Level.FULL).
            target(BasicRaidExperimentalApi.class, props.getRaidoServerUrl());
    }

    public RaidoStableV1Api raidApi(
        final String token
    ){
        return Feign.builder()
            .options(
                new Request.Options(2, TimeUnit.SECONDS, 2, TimeUnit.SECONDS, false)
            )
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder(objectMapper))
            .decoder(new JacksonDecoder(objectMapper))
            .errorDecoder(new RaidApiExceptionDecoder(objectMapper))
            .contract(contract)
            .requestInterceptor(request -> request.header(AUTHORIZATION, "Bearer " + token))
            .logger(new Slf4jLogger(RaidoStableV1Api.class))
            .logLevel(Logger.Level.FULL)
            .target(RaidoStableV1Api.class, props.getRaidoServerUrl());
    }

    public RaidV1Api legacyApi(final String token){
        return Feign.builder().
                client(new OkHttpClient()).
                encoder(new JacksonEncoder(objectMapper)).
                decoder(new JacksonDecoder(objectMapper)).
                contract(contract).
                requestInterceptor(request->
                        request.header(AUTHORIZATION, "Bearer " + token) ).
                logger(new Slf4jLogger(RaidV1Api.class)).
                logLevel(Logger.Level.FULL).
                target(RaidV1Api.class, props.getRaidoServerUrl() + RAID_V1_API);
    }
}