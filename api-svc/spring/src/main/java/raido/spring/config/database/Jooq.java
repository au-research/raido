package raido.spring.config.database;

import raido.util.Log;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static raido.util.Log.to;
import static org.jooq.conf.RenderNameCase.AS_IS;
import static org.jooq.conf.RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED;


@Component
public class Jooq {
  private static Log log = to(Jooq.class);

  @Bean
  public DSLContext dsl(@Autowired ConnectionProvider connectionProvider) {
    log.info("configure dsl()");
    return new DefaultDSLContext(createJooqConfig(connectionProvider));
  }

  private DefaultConfiguration createJooqConfig(
    ConnectionProvider connectionProvider
  ) {
    DefaultConfiguration config = new DefaultConfiguration();
    config.set(connectionProvider);
    config.set(SQLDialect.POSTGRES);
    /** renderSchema=false means schema must be specfied somewhere else 
     @see PrimaryDataSource */
    config.set(new Settings().
      withRenderSchema(true).
      withRenderNameCase(AS_IS).
      withRenderQuotedNames(EXPLICIT_DEFAULT_UNQUOTED));
    config.set(new DefaultExecuteListenerProvider(
      new JooqExceptionTranslator()));
    return config;
  }

}
