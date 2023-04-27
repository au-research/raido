package raido.apisvc.spring.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.impl.DataSourceConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.spring.bean.MetricRegistry;
import raido.apisvc.spring.config.environment.DataSourceProps;
import raido.apisvc.util.Log;

import javax.sql.DataSource;
import java.util.Properties;

import static raido.apisvc.util.Log.to;


@Component
/* Without this @Transactional wasn't working right; 
things were in an autocommit type of state where stuff like 
multi-statement stuff that should have been one big TX were having rollback 
statements issued (cause that's how autocommit works for read-only 
transactions, it does a rollback instead of commit. 
Note that if your API endpoint's transaction seems to be being rolled back 
for no reason - it may be because you've used the Jakarta @Transactional
annotation instead of the Spring one.
*/
@EnableTransactionManagement
/**
 "Primary" in that there are pipe-dreams about having a "read-only" 
 connections and possibly separate queue-service connections.
 */
public class PrimaryDataSource {
  private static final Log log = to(PrimaryDataSource.class);
  
  public static final String MAIN_POOL_NAME = "MainJdbcPool";
  
  @Bean
  public static DataSource hikariDataSource(
    @Autowired DataSourceProps dsProps,
    @Autowired MetricRegistry metricReg
  ) {
    log.debug("initialising HikariCP / Postgres datasource");
    Properties hikariProps = dsProps.createHikariDatasourceProps();
    HikariConfig hikariConfig = new HikariConfig(hikariProps);
    hikariConfig.setPoolName(MAIN_POOL_NAME);
   
    HikariDataSource dataSource = new HikariDataSource(hikariConfig);
    metricReg.registerDataSourceMetrics(dataSource);
    
    return dataSource;
  }

  @Bean
  public DataSourceConnectionProvider connectionProvider(
    @Autowired DataSource dataSource
  ) {
    DataSourceConnectionProvider connectionProvider =
      new DataSourceConnectionProvider(
        new TransactionAwareDataSourceProxy(dataSource));
    log.with("dataSource", dataSource).
      with("connectionProvider", connectionProvider).
      debug("connectionProvider()");
    return connectionProvider;
  }

  /** PlatformTransactionManager */
  @Bean
  public DataSourceTransactionManager transactionManager(
    @Autowired DataSource dataSource
  ) {
    log.with("dataSource", dataSource).debug("transactionManager()");
    return new DataSourceTransactionManager(dataSource);
  }
  
  @Bean
  TransactionTemplate transactionTemplate(
    @Autowired PlatformTransactionManager tm
  ) {
    log.with("transactionManager", tm).info("transactionTemplate()");
    return new TransactionTemplate(tm);
  }

  @Bean
  public JdbcTemplate jdbcTemplate(
    @Autowired DataSource dataSource
  ) {
    return new JdbcTemplate(dataSource);
  }
}
