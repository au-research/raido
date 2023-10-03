package au.org.raid.api.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStubProps {
    @Value("${apids.in-memory-stub:false}")
    public boolean apidsInMemoryStub;

    @Value("${apids.in-memory-stub-delay:150}")
    public long apidsInMemoryStubDelay;

    @Value("${orcid.in-memory-stub:false}")
    public boolean orcidInMemoryStub;

    @Value("${orcid.in-memory-stub-delay:150}")
    public long orcidInMemoryStubDelay;

    @Value("${ror.in-memory-stub:false}")
    public boolean rorInMemoryStub;

    @Value("${ror.in-memory-stub-delay:150}")
    public long rorInMemoryStubDelay;

    @Value("${doi.in-memory-stub:false}")
    public boolean doiInMemoryStub;

    @Value("${doi.in-memory-stub-delay:150}")
    public long doiInMemoryStubDelay;

    @Value("${geonames.in-memory-stub:false}")
    public boolean geoNamesInMemoryStub;

    @Value("${geonames.in-memory-stub-delay:150}")
    public long geoNamesInMemoryStubDelay;

    @Value("${openstreetmap.in-memory-stub:false}")
    public boolean openStreetMapInMemoryStub;

    @Value("${openstreetmap.in-memory-stub-delay:150}")
    public long openStreetMapInMemoryStubDelay;
}
