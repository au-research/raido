The api-svc uses jOOQ for generating SQL, see 
https://www.jooq.org/doc/latest/manual/sql-building/bind-values/sql-injection/.

We use jOOQ API when constructing dynamic queries (i.e. using the jOOQ API 
controlled via conditional logic).

This is considered sufficient protection from SQL injection attacks - manual 
construction of SQL is to be avoided.


  