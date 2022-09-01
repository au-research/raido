package raido.apisvc.spring.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class AppInfoBean {
  private static final Log log = to(AppInfoBean.class);
  
  @Value("${build.version:local}")
  private String buildVersion;
  
  @Value("${build.commitId:local}")
  private String buildCommitId;
  
  @Value("${build.buildDate:local}")
  private String buildDate;

  public String getBuildVersion(){
    return buildVersion;
  }
  
  public String getBuildCommitId(){
    return buildCommitId;
  }
  
  public String getBuildDate(){
    return buildDate;
  }


}
