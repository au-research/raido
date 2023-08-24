package au.org.raid.inttest;

import au.org.raid.api.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

import static au.org.raid.api.util.Log.to;

public class RaidApiExceptionDecoder extends ErrorDecoder.Default {
    private static final Log log = to(RaidApiExceptionDecoder.class);

    private ObjectMapper mapper;

    public RaidApiExceptionDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception feignDefaultEx = super.decode(methodKey, response);

        if (!(feignDefaultEx instanceof FeignException.BadRequest badRequest)) {
            return feignDefaultEx;
        }

        if (badRequest.responseBody().isEmpty()) {
            return feignDefaultEx;
        }

        try {
            byte[] body = badRequest.responseBody().get().array();
            var validEx = mapper.readValue(
                    body, RaidApiValidationException.class);
            validEx.setBadRequest(badRequest);

            log.with("failures", validEx.getFailures()).
                    debug("API ValidationException");

            return validEx;
        } catch (IOException e) {
            log.errorEx("could not map error body", e);
            return feignDefaultEx;
        }

    }
}