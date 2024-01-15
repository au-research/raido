package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.api.model.datacite.DataciteRequest;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataciteRequestFactory {
    private final DataciteDtoFactory dataciteDtoFactory;
    public DataciteRequest create(RaidCreateRequest createRequest, String handle) {
        DataciteDto dataciteDto = dataciteDtoFactory.create(createRequest, handle);

        return DataciteRequest.builder()
                .data(dataciteDto)
                .build();
    }

    public DataciteRequest create(RaidUpdateRequest updateRequest, String handle) {
        DataciteDto dataciteDto = dataciteDtoFactory.create(updateRequest, handle);

        return DataciteRequest.builder()
                .data(dataciteDto)
                .build();
    }

}
