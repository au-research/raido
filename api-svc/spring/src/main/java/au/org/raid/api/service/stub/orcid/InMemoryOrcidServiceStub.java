package au.org.raid.api.service.stub.orcid;

import au.org.raid.api.service.orcid.OrcidService;
import au.org.raid.api.spring.config.environment.InMemoryStubProps;
import au.org.raid.api.util.Log;

import java.util.List;

import static au.org.raid.api.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_ORCID;
import static au.org.raid.api.spring.bean.LogMetric.VALIDATE_ORCID_EXISTS;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.areEqual;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static au.org.raid.api.util.ThreadUtil.sleep;
import static java.util.Collections.emptyList;
import static java.util.List.of;

public class InMemoryOrcidServiceStub extends OrcidService {
    private static final Log log = to(InMemoryOrcidServiceStub.class);

    private InMemoryStubProps stubProps;

    public InMemoryOrcidServiceStub(
            InMemoryStubProps stubProps
    ) {
        super(null);
        this.stubProps = stubProps;
    }

    @Override
    public List<String> validateOrcidExists(String orcid) {
        log.with("delay", stubProps.orcidInMemoryStubDelay).
                debug("simulate ORCID validation check");
        infoLogExecutionTime(httpLog, VALIDATE_ORCID_EXISTS, () -> {
            sleep(stubProps.orcidInMemoryStubDelay);
            return null;
        });

        if (areEqual(orcid, NONEXISTENT_TEST_ORCID)) {
            return of(NOT_FOUND_MESSAGE);
        }

        return emptyList();
    }

}
