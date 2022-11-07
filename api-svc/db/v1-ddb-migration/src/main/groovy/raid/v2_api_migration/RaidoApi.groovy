package raid.v2_api_migration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import feign.Feign
import feign.Logger
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger
import org.springframework.cloud.openfeign.support.SpringMvcContract
import raido.idl.raidv2.api.AdminExperimentalApi
import raido.idl.raidv2.model.ServicePoint

import static org.springframework.http.HttpHeaders.AUTHORIZATION

class RaidoApi {

  String host = System.properties['apiSvcMigrationHost']
  String key = System.properties['apiSvcMigrationKey']

  AdminExperimentalApi adminApi
  
  RaidoApi() {
    println "raido host: " + host
    adminApi = adminExperimentalClient()
  }

  ServicePoint findServicePoint(String name){
    def list = adminApi.listServicePoint()
//    println "name: $name list: $list"
    return list.stream().
      filter(i->name == i.getName() ).
      findFirst().orElseThrow();
  }

  AdminExperimentalApi getAdminApi() {
    return adminApi
  }

  AdminExperimentalApi adminExperimentalClient(){
    def feignContract = new SpringMvcContract()
    def mapper = new ObjectMapper().
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).
        registerModule(new JavaTimeModule());
    
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      requestInterceptor(request->
        request.header(AUTHORIZATION, "Bearer " + key) ).
      logger(new Slf4jLogger(AdminExperimentalApi.class)).
      logLevel(Logger.Level.FULL).
      target(AdminExperimentalApi.class, host);
  }
    
}
