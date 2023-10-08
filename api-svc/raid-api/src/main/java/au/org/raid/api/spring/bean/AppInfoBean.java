package au.org.raid.api.spring.bean;

import au.org.raid.api.util.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static au.org.raid.api.util.Log.to;

@Component
public class AppInfoBean {
    private static final Log log = to(AppInfoBean.class);

    @Value("${build.version:n/a}")
    private String buildVersion;

    @Value("${build.commitId:n/a}")
    private String buildCommitId;

    @Value("${build.buildDate:m/a}")
    private String buildDate;

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getBuildCommitId() {
        return buildCommitId;
    }

    public String getBuildDate() {
        return buildDate;
    }


}
