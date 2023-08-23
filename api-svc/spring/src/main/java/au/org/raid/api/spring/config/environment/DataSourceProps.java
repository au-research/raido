package au.org.raid.api.spring.config.environment;


import au.org.raid.api.util.Log;
import au.org.raid.api.util.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Properties;

import static au.org.raid.api.util.ExceptionUtil.iae;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.hasValue;

/**
 * Gathers up all the different properties Configures the datasource for a
 * Postgres connection with Hikari connection
 * pool.
 * <p/>
 * This class relates only to Datasource stuff (i.e. connection pool / jdbc
 * connection settings).
 * <p/>
 * Understands postgres default credentials (i.e. no password).
 * Understands p6spy driver urls.
 */
@Component
public class DataSourceProps {
    public static final String P6SPY_DRIVER = "com.p6spy.engine.spy.P6SpyDriver";

    private static final Log log = to(DataSourceProps.class);

    @Value("${DatasourceConfig.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${DatasourceConfig.url}")
    private String url;

    @Value("${DatasourceConfig.username}")
    private String username;

    @Value("${DatasourceConfig.password}")
    private String password;

    /* I think this is unused in raido ATM, because we use schema specifiers
    driven from Jooq - but it will be needed if we need to use native SQL. */
    @Value("${DatasourceConfig.schema:api_svc}")
    private String schema;

    /**
     * Problems in this area often manifest with a symptom like:
     * "HikariPool-1 - Connection is not available, request timed out after XXXms."
     * The XXX will be whatever the connectionTimeout is.
     * <p/>
     * But the problem is likely not the actual size of the pool, it's
     * usually that we're holding connections too long.<br/>
     * Either because of bad SQL/indexes, undersized/misconfigured DB, or bad
     * code that's holding transactions/connections over external calls or other
     * in-process wait conditions, etc.
     * <p/>
     * As of early 2019, "show max_connections" with default RDS params gave:<br/>
     * - db.t2.micro - 87<br/>
     * - db.t2.medium - 413<br/>
     * - db.t3.small - 193<br/>
     * <p>
     * This might be better these days, after PG 14/15 connection optimisation work.
     * <br/>
     * Consider that we may want to run multiple instances and want to
     * do zero-downtime updates, even under load.
     * <p/>
     * The current dodgy "zero-downtime upgrade" that we do involves doubling the
     * ASG count, waiting till all nodes come up, then halving the ASG count.
     * The practical result of that is that, under enough load the all the instances
     * are using all their connections - your peak connection usage during an
     * upgraded is twice your normal peak usage.
     * <p/>
     * Hikari default is 10.  Even 20 may be quite oversized, see
     * https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing.
     * Especially while we're only running dinky little t3.micro instances.
     * Initial load testing was showing a single t2.micro was sitting at
     * about 50% cpu.<br/>
     * This likely needs re-thinking (definitely needs re-testing) for any
     * changes to DB size or EC2 size/instance count.
     */
    @Value("${DatasourceConfig.maximumPoolSize:20}")
    private String maximumPoolSize;

    /**
     * Connection acquisition timeout in milliseconds.
     * Hikari: "max time in ms client will wait for a connection from the pool".
     * <p/>
     * Don't just bump this when a long-running query causes problems.  That
     * just results in "AppServer threads + DB connection pool" being treated
     * as a poorly implemented work queue.
     * <p/>
     * Consider that bumping connectionTimeout directly increases the
     * "dead-time" the application gives when a DB failover happens.
     * <p/>
     * Resolve the root-cause of the long running queries:
     * <ul>
     * <li>index, rewrite or even denormalise bad queries</li>
     * <li>increase CPU, RAM, IO or whatever of the DB</li>
     * <li>manage long running queries with a proper job queue
     * <ul>
     * <li>preferably working off of a separate read-replica DB on a
     * dedicated job processing</li>
     * <li>at least working off its own connection pool</li>
     * </ul>
     * </li>
     * </ul>
     */
    @Value("${DatasourceConfig.connectionTimeout:4000}")
    private String connectionTimeout;

    /**
     * default 10 in an attempt to mitigate the blip at start of load testing.
     */
    @Value("${DatasourceConfig.minimumIdle:10}")
    private String minimumIdle;

    /**
     * Hikari: "maximum amount of time that a connection is allowed to sit idle
     * in the pool".
     * Minimum this can be is 10 seconds.
     * Current default is 5 min (Hikari default is 10 min).
     * Want to keep it reasonable so that it's easier to avoid connection
     * exhaustion in the DB when cycling instances.
     * Actually, probably need to confirm what the idle timeout is inside the
     * DB too.
     */
    @Value("${DatasourceConfig.idleTimeout:30000}")
    private String idleTimeout;

    /**
     * Workaround for stale SQL statements in cache; problems manifests after
     * doing a zero-downtime schema change as "ERROR: cached plan must not change
     * result type".
     * <p>
     * Once a statement is cached, if any schema change issues DDL that changes
     * DB types for that query, the next time we run the cached SQL statement,
     * it will fail with:
     * "ERROR: cached plan must not change result type".
     * <p>
     * This setting resolves that by setting "autosave=conservative"
     * at the JDBC level.
     *
     * @see <a href="https://stackoverflow.com/a/48536394/924597">
     * https://stackoverflow.com/a/48536394/924597
     * </a>
     */
    @Value("${DatasourceConfig.statementCacheFix:true}")
    private boolean statementCacheFix;

    /**
     * "number of PreparedStatement executions required before switching over
     * to use server side prepared statements".
     * Pgjdbc default is 5.
     */
    @Value("${DatasourceConfig.prepareThreshold:5}")
    private String prepareThreshold;

    /**
     * Current default is 0, Hikari default is 0 (0 means disabled)
     */
    @Value("${DatasourceConfig.leakDetectionThreshold:0}")
    private String leakDetectionThreshold;

    /**
     * Will return the p6spy driver if given a p6spy url, otherwise returns
     * defaultDriver given.
     */
    public static Optional<String> overrideDriverForP6spy(
            String jdbcUrl,
            @Nullable String defaultDriver) {
        if (jdbcUrl == null) {
            return Optional.ofNullable(defaultDriver);
        }

        if (jdbcUrl.contains("p6spy")) {
            return Optional.of(P6SPY_DRIVER);
        } else {
            return Optional.ofNullable(defaultDriver);
        }
    }

    public Properties createHikariDatasourceProps() {
        Properties hikariConfig = new Properties();

        String configUrl = url;
        if (hasValue(schema)) {
            log.debug("adding currentSchema driver parameter");
            configUrl = appendCurrentSchema(configUrl);
        }
        log.info("DB url: %s", configUrl);
        hikariConfig.put("jdbcUrl", configUrl);

    /* Setting the hikari schema property doesn't work, see:
    https://github.com/brettwooldridge/HikariCP/issues/1633 */
        // hikariConfig.put("schema", "api_svc");

        Optional<String> driver = overrideDriverForP6spy(configUrl, driverClassName);
        if (driver.isPresent()) {
            log.info("DB driverClassName: %s", driver.get());
            hikariConfig.put("driverClassName", driver.get());
        } else {
            log.info("DB driverClassName not specified");
        }

        log.info("DB connectionTimeout: %s", connectionTimeout);
        hikariConfig.put("connectionTimeout", connectionTimeout);

        log.info("DB maximumPoolSize: %s", maximumPoolSize);
        hikariConfig.put("maximumPoolSize", maximumPoolSize);

        log.info("DB minimumIdle: %s", minimumIdle);
        hikariConfig.put("minimumIdle", minimumIdle);

        log.info("DB idleTimeout: %s", idleTimeout);
        hikariConfig.put("idleTimeout", idleTimeout);

        log.info("DB leakDetectionThreshold: %s", leakDetectionThreshold);
        hikariConfig.put("leakDetectionThreshold", leakDetectionThreshold);

        hikariConfig.put("autoCommit", "false");

        // makes sure app will start even if you stand it up when the DB is down
        hikariConfig.put("initializationFailTimeout", "0");

        hikariConfig.put("dataSourceProperties", createPostgresDatasourceProps());

        return hikariConfig;
    }

    private String appendCurrentSchema(String configUrl) {
        if (configUrl.toLowerCase().contains("currentschema")) {
            throw iae("url already contains currentSchema: %s", configUrl);
        }

        if (configUrl.contains("?")) {
            return configUrl + "&currentSchema=api_svc";
        } else {
            return configUrl + "?currentSchema=api_svc";
        }
    }

    /**
     * @see <a href="https://jdbc.postgresql.org/documentation/head/connect.html#connection-parameters">
     * Postgres JDBC connection parameters.
     * </a>
     */
    private Properties createPostgresDatasourceProps() {
        Properties postgresDatasourceConfig = new Properties();

        log.info("DB user: %s", username);
        postgresDatasourceConfig.put("user", username);
        if (hasValue(password)) {
            postgresDatasourceConfig.put("password", password);
        } else {
            log.warn("DEVELOPMENT MODE - no database password supplied");
            postgresDatasourceConfig.put("password", "");
        }

        // disable database creation/modification, not sure
        postgresDatasourceConfig.put("initialize", "false");

        postgresDatasourceConfig.setProperty("prepareThreshold", prepareThreshold);

        if (statementCacheFix) {
            postgresDatasourceConfig.setProperty("autosave", "conservative");
        }

        return postgresDatasourceConfig;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
}
