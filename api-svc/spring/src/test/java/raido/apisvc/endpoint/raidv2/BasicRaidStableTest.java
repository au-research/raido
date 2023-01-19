package raido.apisvc.endpoint.raidv2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import raido.apisvc.service.raid.RaidService;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

@SpringJUnitWebConfig(BasicRaidStableTest.Config.class)
class BasicRaidStableTest {

  private MockMvc mockMvc;

  @Autowired
  private RaidService raidSvc;


  @BeforeEach
  void setup(WebApplicationContext context) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void readRaidV1_ReturnsOk() {
    fail("unimplemented");
  }

  @Configuration
  static class Config {
    @Bean
    RaidService raidService() {
      return mock(RaidService.class);
    }


  }
}