
## Use bigint identity for surrogate keys

You can use a natural key as your primary key if it makes sense to you.

When using a surrogate key though, this is the default approach:
```sql
create table xxx (
  id bigint generated always as identity
    (start with 1)
    primary key not null
)
```

The `start with 1` clause is about avoiding having different tables
using the same identities, so that accidental transposition errors 
(because all the table surrogate keys have the same type) will fail quickly and
are likely to be detected early.

`bigint` is 64bits, plenty of space to spare. 
Don't have do it if you don't want to.


## Specify max legth for text columns

It's easy to bump it up when you need to.
Hard to reduced it down once people abuse it and put giant things
in there that you weren't expecting.

If in doubt, use `VARCHAR(256)`.  There's nothing special about `256` and 
there's no logical reason to use powers of 2 for character lengths.  It's 
just a silly little forcing function, like using fibonacci numbers for story
points.  

If you know you need more, or know your exactly limit, then do it.

For large values, before worrying about factoring out to a separate table, learn
about postgrest TOAST.  Then think about separating it out anyway :)


## Unique constraints

## Indexes


