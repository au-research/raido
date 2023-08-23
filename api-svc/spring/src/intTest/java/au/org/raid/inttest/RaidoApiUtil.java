package au.org.raid.inttest;

import au.org.raid.idl.raidv2.api.PublicExperimentalApi;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 convenience stuff for doing common stuff with the Raido API.
 Normally would be all static methods, but I don't think it's a good idea 
 to be constantly making new API client instantiations (currently OpenFeign).
 
 Do the wrappers really matter?
 */
public class RaidoApiUtil {

  private PublicExperimentalApi publicApi;
  private ObjectMapper mapper;
  
  public RaidoApiUtil(PublicExperimentalApi publicApi, ObjectMapper mapper) {
    this.publicApi = publicApi;
    this.mapper = mapper;
  }

  public PublicExperimentalApi getPublicExperimental() {
    return publicApi;
  }
}
