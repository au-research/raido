package au.org.raid.api.spring.config.database;

import au.org.raid.api.util.Log;
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

import static au.org.raid.api.util.Log.to;
import static org.jooq.conf.RenderNameCase.AS_IS;
import static org.jooq.conf.RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED;


@Component
public class Jooq {
    private static Log log = to(Jooq.class);

    public static DefaultConfiguration createJooqConfig(
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
                // Raido uses p6spy for SQL and transaction stuff - not just JOOQ stuff
                        withExecuteLogging(false).
                withRenderQuotedNames(EXPLICIT_DEFAULT_UNQUOTED));
        config.set(new DefaultExecuteListenerProvider(
                new JooqExceptionTranslator()));
        return config;
    }

    @Bean
    public DSLContext dsl(@Autowired ConnectionProvider connectionProvider) {
        log.with("connectionProvider", connectionProvider).debug("configure dsl()");
        return new DefaultDSLContext(createJooqConfig(connectionProvider));
    }

}
