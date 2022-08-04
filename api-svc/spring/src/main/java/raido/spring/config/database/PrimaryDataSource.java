package raido.spring.config.database;

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
import raido.spring.config.environment.DataSourceProps;
import raido.util.Log;

import javax.sql.DataSource;
import java.util.Properties;

import static raido.util.Log.to;


@Component
/* Without this @Transactional wasn't working right; 
things were in an autocommit type of state where stuff like 
Hibp TXs that should have been one big TX were having rollback 
statements issued (cause that's how autocommit works for read-only 
transactions, it does a rollback instead of commit.*/
@EnableTransactionManagement
/**
 "Primary" in that there are pipe-dreams about having a "read-only" 
 connections and possibly separate queue-service connections.
 */
public class PrimaryDataSource {
  private static final Log log = to(PrimaryDataSource.class);
  
  @Bean
  public static DataSource hikariDataSource(
    @Autowired DataSourceProps config
  ) {
    log.debug("initialising HikariCP / PostgreSQL datasource");
    Properties hikariProps = config.createHikariDatasourceProps();
    return new HikariDataSource(new HikariConfig(hikariProps));
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

  @Bean
  public DataSourceTransactionManager transactionManager(
    @Autowired DataSource dataSource
  ) {
    log.with("dataSource", dataSource).debug("transactionManager()");
    // DSTM is a PlatformTransactionManager
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
