package au.org.raid.api.spring.config.environment;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// improve: need to rename property keys that were moved from EnvProps.
@Component
@Data
public class MetadataProps {

    @Value("${raid.id.license:Creative Commons CC-0}")
    private String raidlicense;

    /**
     * the "front door" (expected to be CloudFront or possibly even CloudFlare)
     * for the raid landing page.
     * Controls the url that the global handle registry will redirect to (that's
     * why it uses the front door, instead of referencing the API).
     * The default value is for local dev, it's the the node process serving the
     * app-client via running `npm run start`, so that local testing works.
     * demo is set to `https://demo.raido-infra.com/raid`.
     */
    @Value("${EnvironmentConfig.raidoLandingPrefix:" +
            "http://localhost:7080/handle}")
    private String raidoLandingPrefix;

    @Value("${EnvironmentConfig.globalUrlPrefix:" +
            "https://hdl.handle.net}")
    private String globalUrlPrefix;

    /**
     * For Raido, this is the RoR of the ARDC.
     */
    @Value("${EnvironmentConfig.identifierRegistrationAgency:" +
            "https://ror.org/038sjwq14}")
    private String identifierRegistrationAgency;

    /**
     * demo: demo.raid.org.au
     * prod: raid.org.au
     */
    @Value("${MetadataConfig.handleUrlPrefix:" +
            "http://localhost:8080}")
    private String handleUrlPrefix;

    /**
     * The servicelevel guide currently says "200 KB maximum size".
     * This is chars, but we expect mostly ASCII for a while with Raido, so this'll
     * do.  It's set small in dev so we can see it easier, and so I can see
     * if data migration hits it.
     */
    @Value("${MetadataConfig.maxMetadataChars:10240}")
    private long maxMetadataChars;
}
