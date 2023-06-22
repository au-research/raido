This page records the Postgres specific tech Raido uses.

Makes a good starting point for "how much work to port Raido to a different DB".


# Current PG features used

* Jsonb for storing raid metadata envelope
* Custom enum types, specifically enums like: `user_role`
  * see [V4__sign_in_tables.sql](../db/raido/src/main/resources/db/migration/V4__sign_in_tables.sql)


# Future PG features to use

* skip lock for queuing
  * cheap and easy to implement
  * cheap to operate at low/mid-scale
  * upgrade path for increasing scale
    * separate DB
      * easy to implement because of already serialising Q items to DB
    * real Qing solution
      * easy to implement because of already serialising Q items to DB
* enums and arrays
  * for ease of use of implementation 
  * tuple compactness, leading to improved performance 
    (more stuff in memory for each page)
  * downside: enums were/are a pain to evolve over time
    * getting better and we know how to do it
    * worth the effort
* jsonb
  * for metadata envelope and anything else we don't want to model relationally
    * especially, auditing history over multiple schema versions
  * not currently planning on converting the schema to an entity-value model
  * querying into the metadata can be easily supported by 
    postgres' json support (including proper indexing)
* modern/advanced SQL
  * CTEs, recursive queries
  * window functions
  * etc.
* zero downtime schema migration
  * postgres is better at schema level - concurrent index creation, 
    partial indexes, etc.
  * postgres still is not good at upgrading postgres version level though - 
    but we don't have to fiddle with it (RDS takes care of it for us, 
    but with downtime)
* triggers
  * we're going to use triggers for auditing
    * in Postgres, this is very easy and fairly secure 
    (much better than application-level auditing)
  * also plan to use triggers for "deferred data migration" when necessary 
  (mid-high scale)
  i.e. do a schema migration sometimes via multi-step release process;
  zero-downtime schema migration that's fast because we change schema but 
  leave field optionality open
  run for a while with triggers updating any values that are read or updated
  run background process to migrate / archive "untouched" rows
  run final schema migration that does minimal table writing and locks down schema


# Past PG features used

* native UUID as keys
  * this was the original plan, but I decided to default to using `bigint` 
    instead, see [schema-guideline.md](../db/raido/doc/schema-guideline.md) 
  * UUID v6, i think?
  * designed for use as DB identifier, i.e. ordering characteristics, etc.
  * high storage efficiency
  * DB index friendly
  * not globally bottlenecked
  * minimal generation overhead
  * shard friendly

