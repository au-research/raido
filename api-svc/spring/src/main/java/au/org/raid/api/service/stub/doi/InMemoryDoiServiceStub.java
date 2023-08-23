package au.org.raid.api.service.stub.doi;

import au.org.raid.api.service.doi.DoiService;
import au.org.raid.api.spring.config.environment.InMemoryStubProps;
import au.org.raid.api.util.Log;

import java.util.List;

import static au.org.raid.api.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_DOI;
import static au.org.raid.api.spring.bean.LogMetric.VALIDATE_DOI_EXISTS;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.areEqual;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static au.org.raid.api.util.ThreadUtil.sleep;
import static java.util.Collections.emptyList;
import static java.util.List.of;

public class InMemoryDoiServiceStub extends DoiService {
    private static final Log log = to(InMemoryDoiServiceStub.class);

    private InMemoryStubProps stubProps;

    public InMemoryDoiServiceStub(
            InMemoryStubProps stubProps
    ) {
        super(null);
        this.stubProps = stubProps;
    }

    @Override
    public List<String> validateDoiExists(String doi) {
        log.with("delay", stubProps.doiInMemoryStubDelay).
                debug("simulate DOI validation check");
        infoLogExecutionTime(httpLog, VALIDATE_DOI_EXISTS, () -> {
            sleep(stubProps.doiInMemoryStubDelay);
            return null;
        });

        if (areEqual(doi, NONEXISTENT_TEST_DOI)) {
            return of(NOT_FOUND_MESSAGE);
        }
        return emptyList();
    }
}
