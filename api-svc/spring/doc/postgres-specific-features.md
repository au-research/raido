This page records the Postgres specific tech Raido uses.

Makes a good starting point for "how much work to port Raido to a different DB".


# Current PG features used

* Jsonb for storing original raid-v1 DDB data.
* UUID for primary keys


# Future PF features to use

* skip lock for queuing
  * cheap and easy to implement
  * cheap to operate at low/mid-scale
  * upgrade path for increasing scale
    * separate DB
      * easy to implement because of already serialising Q items to DB
    * real Qing solution
      * easy to implement because of already serialising Q items to DB
* native UUID as keys
  * UUID v6, i think?
  * designed for use as DB identifier, i.e. ordering characteristics, etc.
  * high storage efficiency
  * DB index friendly
  * not globally bottlenecked
  * minimal generation overhead
  * shard friendly
* enums and arrays
  * for ease of use on my side
  * tuple compactness, leading to improved performance 
    (more stuff in memory for each page)
  * downside: enums were/are a pain to evolve over time
    * getting better and I know how to do it
    * worth the effort
* jsonb
  * for the DMR, maybe
  * and anything else we don't want to model relationally
    * especially, auditing history over multiple schema versions
  * I'm not currently planning on converting the schema to an entity-value model
  * the occasional need to query into the DMR can be easily supported by 
    postgres' json support (including proper indexing)
* modern/advanced SQL
  * CTEs
  * window functions
  * etc.
* zero downtime schema migration
  * postgres is better at schema level - concurrent index creation, 
    partial indexes, etc.
  * postgres still is not good at upgrading postgres version level though - 
    but we don't have to fiddle with it (RDS takes care of it for us, 
    but with downtime)
* triggers
  * apparently mysql trigger support is limited?
    * even with my personal biases, that's surprising
    * need to research this don't want to state it if not true
  * we're going to use triggers for auditing
    * in Postgres, this is very easy and fairly secure 
    (much better than application-level auditing)
  * also plan to use triggers for "deferred data migration" when necessary 
  (mid-high scale)
  i.e. do a schema migration sometimes via multi-step release process
  zero-downtime schema migration that's fast because we change schema but 
  leave field optionality open
  run for a while with triggers updating any values that are read or updated
  run background process to migrate / archive "untouched" rows
  run final schema migration that does minimal table writing and locks down schema


# Past PG features used

* none yet