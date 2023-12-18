package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.*;
import au.org.raid.idl.raidv2.model.Date;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataciteAttributesDtoFactory {
    private final DataciteTitleFactory dataciteTitleFactory;
    private final DataciteCreatorFactory dataciteCreatorFactory;
    private final DataciteDateFactory dataciteDateFactory;

    @SneakyThrows
    public DataciteAttributesDto create(RaidCreateRequest request, String handle) {
        if (request == null) {
            return null;
        }

        List<DataciteTitle> dataciteTitles = null;
        if (request.getTitle() != null) {
            dataciteTitles = request.getTitle().stream()
                    .map(dataciteTitleFactory::create)
                    .toList();
        }

        List<DataciteCreator> dataciteCreators = null;
        if (request.getIdentifier() != null) {
            dataciteCreators = new ArrayList<>();
            dataciteCreators.add(dataciteCreatorFactory.create(request.getIdentifier().getRegistrationAgency()));
        }

        List<DataciteDate> dataciteDates = null;
        if (request.getDate() != null) {
            Date raidDates = request.getDate();
            dataciteDates.add(dataciteDateFactory.create(raidDates));
        }

        String prefix = handle.split("/")[0];

        return new DataciteAttributesDto()
                .setPrefix(prefix)
                .setDoi(handle)
                .setTitles(dataciteTitles)
                .setCreators(dataciteCreators)
                .setDates(dataciteDates);
    }

    @SneakyThrows
    public DataciteAttributesDto create(RaidUpdateRequest request, String handle) {
        if (request == null) {
            return null;
        }

        List<DataciteTitle> dataciteTitles = null;
        if (request.getTitle() != null) {
            dataciteTitles = request.getTitle().stream()
                    .map(dataciteTitleFactory::create)
                    .toList();
        }

        List<DataciteCreator> dataciteCreators = null;
        if (request.getIdentifier() != null) {
            dataciteCreators = new ArrayList<>();
            dataciteCreators.add(dataciteCreatorFactory.create(request.getIdentifier().getRegistrationAgency()));
        }

        String prefix = handle.split("/")[0];
        String suffix = handle.split("/")[1];

        return new DataciteAttributesDto().setPrefix(prefix).setDoi(handle).setTitles(dataciteTitles).setCreators(dataciteCreators);
    }

}
