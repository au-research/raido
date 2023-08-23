package au.org.raid.api.spring.config.environment;

import au.org.raid.api.util.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static au.org.raid.api.util.Log.to;

@Component
public class ApidsProps {
    private static final Log log = to(ApidsProps.class);

    @Value("${Apids.secret:}")
    public String secret;

    @Value("${Apids.appId:57158ed6a84b87b23b23cbf3016d59786fb2de5a}")
    public String appId;

    @Value("${Apids.serviceUrl:https://demo.identifiers.ardc.edu.au/pids}")
    public String serviceUrl;

}
