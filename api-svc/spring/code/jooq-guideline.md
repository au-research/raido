
# jOOQ guidelines

## Avoid usage of `DSL.asterisk()`

When you use `DSL.asterisk()` - it causes jooq to issue SQL of the form
`select * from table`.
jooq normally issues sql like: 
`select column1, column2, column3 from table`.

When you use the `*` with jooq like this, it means the your app code will not
be forwards compatible with new versions of the DB schema that contain new 
columns, and thus you should avoid it's usage where possible.

It means when you do a deployment, for the time between when you run the 
`flywayMigreate` command and until you deploy your new app code, the queries 
that explicitly contain an `*` will break if there's a new column in the DB 
schema.


### Why does using a `*` in SQL break forward-compatibility?

When jooq parses the results from a SQL statement, it parse each column
result value to the type that it knows from the generated schema.

Normally, jooq generates sql queries that explicitly list out the columns 
that it knows about (thus extra "unknown" columns are ignored). But by 
using the `*` explicitly you force jooq to issue SQL that will return all 
columns in the DB (including new ones from a later version of the schema that
your jooq/app code doesn't know about).  

The parsing of the result set then fails when it comes across the new column 
that it doesn't know anything about.

I'm not sure why it can't just ignore a column it doesn't know about?  Maybe
there's a config option for that, not sure.
