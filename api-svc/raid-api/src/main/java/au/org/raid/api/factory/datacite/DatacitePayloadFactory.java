package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteAttributesDto;
import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatacitePayloadFactory {

    private final DataciteDtoFactory dataciteDtoFactory;
    private final DataciteAttributesDtoFactory dataciteAttributesDtoFactory;

    public DataciteDto payloadForCreate(RaidCreateRequest request, String handle) throws JsonProcessingException {
        DataciteAttributesDto dataciteAttributesDto = dataciteAttributesDtoFactory.create(request, handle);
        DataciteDto dataciteDto = dataciteDtoFactory.create(request, handle);
        ObjectMapper objectMapper = new ObjectMapper();
        return dataciteDto;
    }

    public DataciteDto payloadForUpdate(RaidUpdateRequest request, String handle) throws JsonProcessingException {
        DataciteAttributesDto dataciteAttributesDto = dataciteAttributesDtoFactory.create(request, handle);
        DataciteDto dataciteDto = dataciteDtoFactory.create(request, handle);
        ObjectMapper objectMapper = new ObjectMapper();
        return dataciteDto;
    }
}