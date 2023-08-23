package au.org.raid.api.spring.config.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

import java.sql.SQLException;

import static au.org.raid.api.util.Log.to;

/**
 * Transforms {@link SQLException} into a Spring-specific
 * {@link DataAccessException}.
 * <p>
 * Copied from org.springframework.boot.autoconfigure.jooq
 * Also see https://www.baeldung.com/jooq-with-spring#1-translating-jooq-exceptions-to-spring
 */
public class JooqExceptionTranslator implements ExecuteListener {

    // Based on the jOOQ-spring-example from https://github.com/jOOQ/jOOQ

    private static final Log logger = LogFactory.getLog(JooqExceptionTranslator.class);

    @Override
    public void exception(ExecuteContext context) {
        SQLExceptionTranslator translator = getTranslator(context);
        // The exception() callback is not only triggered for SQL exceptions but also for
        // "normal" exceptions. In those cases sqlException() returns null.
        SQLException exception = context.sqlException();
        while (exception != null) {
            handle(context, translator, exception);
            exception = exception.getNextException();
        }
    }

    private SQLExceptionTranslator getTranslator(ExecuteContext context) {
        SQLDialect dialect = context.configuration().dialect();
        if (dialect != null && dialect.thirdParty() != null) {
            String dbName = dialect.thirdParty().springDbName();
            if (dbName != null) {
                return new RaidoSQLErrorCodeSQLExceptionTranslator(dbName);
            }
        }
        return new SQLStateSQLExceptionTranslator();
    }

    /**
     * Handle a single exception in the chain. SQLExceptions might be nested multiple
     * levels deep. The outermost exception is usually the least interesting one ("Call
     * getNextException to see the cause."). Therefore the innermost exception is
     * propagated and all other exceptions are logged.
     *
     * @param context    the execute context
     * @param translator the exception translator
     * @param exception  the exception
     */
    private void handle(ExecuteContext context, SQLExceptionTranslator translator, SQLException exception) {
        DataAccessException translated = translate(context, translator, exception);
        if (exception.getNextException() == null) {
            context.exception(translated);
        } else {
            logger.error("Execution of SQL statement failed.", translated);
        }
    }

    private DataAccessException translate(ExecuteContext context, SQLExceptionTranslator translator,
                                          SQLException exception) {
        return translator.translate("jOOQ", context.sql(), exception);
    }

    /**
     * I have a feeling this is not a great implementation of this.
     * see https://stackoverflow.com/a/66283045/924597
     * Try to find out if there's not a better implementation of this somewhere in
     * the jooq codebase that I can use.
     */
    private static class RaidoSQLErrorCodeSQLExceptionTranslator
            extends SQLErrorCodeSQLExceptionTranslator {
        private static final au.org.raid.api.util.Log log = to(
                RaidoSQLErrorCodeSQLExceptionTranslator.class);

        public RaidoSQLErrorCodeSQLExceptionTranslator(String dbName) {
            super(dbName);
        }

        @Override
        protected DataAccessException customTranslate(
                String task, String sql, SQLException sqlEx
        ) {
            // super of this just returns null which stomps the sqlEx
            var translated = super.customTranslate(task, sql, sqlEx);
            if (translated != null) {
                return translated;
            }

            return new UncategorizedSQLException(task, sql, sqlEx);
        }
    }
}

