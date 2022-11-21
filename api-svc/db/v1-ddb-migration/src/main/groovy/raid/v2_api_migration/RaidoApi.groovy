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
import org.jooq.Record
import org.springframework.cloud.openfeign.support.SpringMvcContract
import raido.idl.raidv2.api.AdminExperimentalApi
import raido.idl.raidv2.model.*

import static db.migration.jooq.tables.Raid.RAID
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

  static MetadataSchemaV1 mapToMetadataSchemaV1(
    Record raid
  ) {
    var startDate = raid.get(RAID.START_DATE)

    var contentPath = raid.getValue(RAID.CONTENT_PATH)
    List<AlternateUrlBlock> urls = null;
    if( contentPath !== "https://raid.org.au" ){
      urls = new ArrayList<>()
      urls.add(new AlternateUrlBlock().url(contentPath))
    }

    return new MetadataSchemaV1().
      metadataSchema(RaidoMetaschema.PUBLICMETADATASCHEMAV1).
      id(new IdBlock().
        identifier(raid.get(RAID.HANDLE)).
        identifierTypeUri("https://raid.org").
        globalUrl(contentPath)).
      access(new AccessBlock().
        type(AccessType.CLOSED).
        accessStatement("closed by data migration process")).
      dates(new DatesBlock().startDate(startDate.toLocalDate())).
      titles([new TitleBlock().
        type(TitleType.PRIMARY_TITLE).
        title(raid.getValue(RAID.NAME)).
        startDate(startDate.toLocalDate())]).
      descriptions([new DescriptionBlock().
        type(DescriptionType.PRIMARY_DESCRIPTION).
        description(raid.get(RAID.DESCRIPTION))]).
      alternateUrls(urls)

  }
}
  
