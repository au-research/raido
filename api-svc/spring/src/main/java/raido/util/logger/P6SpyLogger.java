package raido.util.logger;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.Slf4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This custom p6spy logger redirects certain kinds of p6spy log statements
 * to different log categories, so that we can do different things with those
 * statements (generally, so we can filter them out, but you could do 
 * other things like log them to a different location or something).
 * <p/>
 * P6Spy usually logs at debug to the "p6spy" category, this class redirects
 * two categories of info to "sub-categories":
 * <ul>
 *   <li>transaction stuff ("commit"/"rollback") will get logged to "p6spy.tx"
 *   </li>
 *   <li>connection test stuff ("select 1" statements) will get logged to 
 *   "p6spy.cxnTest" category.
 *   </li>
 * </ul>
 * Tell p6spy to use the class by setting 
 * "appender=...logger.P6SpyLogger" in your spy.properties.
 * Then you just tell the log framework what you want to do with statements 
 * of that category in your normal log config (I usually set the log category
 * to "info" in order to filter out the debug logs).
 * <p/>
 * The logger also reduces the amount of SQL being logged in each line by not 
 * logging the original SQL statement (the one with the param bindings in it) 
 * - it only logs the SQL statement that shows the statement parameters 
 * bound into the SQL.
 */
public class P6SpyLogger extends Slf4JLogger {
  public static final String SPY_LOGGER_PREFIX = "p6spy";
  public static final String TX_LOGGER = SPY_LOGGER_PREFIX + ".tx";
  public static final String CXN_LOGGER = SPY_LOGGER_PREFIX + ".cxnTest";

  private Logger txLog;
  private Logger cxnTest;

 	public P6SpyLogger() {
    LoggerFactory.getLogger(SPY_LOGGER_PREFIX).info("init");
 	  txLog = LoggerFactory.getLogger(TX_LOGGER);
 	  cxnTest = LoggerFactory.getLogger(CXN_LOGGER);
 	}

  @Override
  public void logSQL(
    int connectionId,
    String now,
    long elapsed,
    Category category,
    String prepared,
    String sql,
    String jdbcUrl
  ) {

 	  // we only use one URL - so don't log because it just adds noise
 	  jdbcUrl = "";

 	  // handle connection pool "connect test" statements
    if( 
      category == Category.STATEMENT && 
      "select 1".equalsIgnoreCase(prepared)
    ){
      if( cxnTest.isDebugEnabled() ){
        cxnTest.debug(strategy.formatMessage(
          connectionId, now, elapsed, category.toString(),
          prepared, sql, jdbcUrl ));
      }
      return;
    }

    // handle "tx" category logging 
    if( category == Category.ROLLBACK || category == Category.COMMIT ){
      if( txLog.isDebugEnabled() ){
        txLog.debug(strategy.formatMessage(
          connectionId, now, elapsed, category.toString(),
          prepared, sql, jdbcUrl ));
      }
      return;
    }
    
    if( category == Category.STATEMENT ){
      /* I prefer SQL to be logged as DEBUG because I like to "dim"
       debug in my test output */
      category = Category.DEBUG;
    }
    
    // don't print the first "statement" SQL, it's annoying to have them both
    prepared = "";

    super.logSQL(connectionId, now, elapsed, category, prepared, sql, jdbcUrl);
  }
  
}
