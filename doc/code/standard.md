
# Coding standards

> Just do it.

If a tool/library can't support these standards - that's a stronger argument 
for discarding the tool than it is for making an exception.


## Do not put credentials in source repositories

This isn't about whether credentials are hard-coded or not.
It's about minimising risk, given we're using a public hosting service and
have public repositories.

Credentials shouldn't be anyway near the source code.
They shouldn't be in a sibling directory to your source tree,
they shouldn't even be within the same authorization scope.

TODO:STO should operational security page be public?
See "RAID - Operational security" for more info about how
developer machines, AWS machines and credentials should be managed.


### Explicitly disallowed credential strategies

#### Ignore file entries are not acceptable

Too easy to make mistakes.

#### Encrypting credentials is not acceptable

Usually leads to "encraption", where the key is stored right next to the
payload.
If you're practicing good hygiene for your encryption key, then just do that
with the credentials directly and spare us the crypto security theatre


## Don't log sensitive data

Be careful of logging headers, they may contain an Authorization header,
which contains the authn/authz token.
Deal with header logging explicitly/safely - or don't log them at all.

That's about it for sensitive data for us at the moment?
If you do write some code that you think might log sensitive data
(tokens, etc.) directly or indirectly mark it with a SECURITY:XXX tag
to make sure it's dealt with before a release - acknowledged as an accepted
risk, mitigated or removed


## Use UTF-8 encoding for all non-ASCII content

Any time you serialise text data: across the wire, into a html attribute,
to disk, to a local store, to a DB column.

This includes source files, resources, everything in git, and in the DB -
all should be utf-8.

Yes - Windows, Java and browsers are usually UTF-16 internally.  Use UTF-8
everywhere there is a choice.

This includes source, markdown and other resources.  
Emojis are a crucial documentation tool 🤠 ❤️‍🔥


### Postgres
The actual encoding of `TEXT`/`VARCHAR` values depends on the encoding config
of the DB.

Raido database has these config values:
* `server_encoding=UTF8` 
* `client_encoding=UTF8` 
* `lc_collate=en_US.utf8`

Remember that UTF8 is variable width - postgres text operations work in terms
of characters not bytes.
```sql
select length('A') -- length = 1
union
select length('🏃‍♂️') -- length = 2
union
select length('❤️') -- length = 4
union
select length('👩‍❤️‍💋‍👩') -- length = 8
```


## Use ASCII for non-content (URLs, filenames)

"ASCII" means 7-bit clean US-ASCII - not the endless swamp of integration
issues that is extended ASCII.

Raido follows "utf-8 everywhere" where possible, but one of the places where
things get sticky is filenames, directories, container names, resource names,
urls (domain names too), etc.

Cross-platform support for non-ascii characters in non-content places like
filenames is an awkward, theoretically-solved-but-not-really mess that we don't
want to waste time on. This lack of standardisation and clarity sometimes
affects things you might not expect, like Git repo names and paths and stuff.

This guidelines isn't about establishing what platform does which operation
right/wrong (pro-tip: Microsoft is almost always "wrong") or advocating
for/against standards issues.
This is about us avoiding spending time dealing with niggling little issues
around weird edge-cases.
Stuff like this: https://stackoverflow.com/a/22828826/924597, whee!

If you use funky names - nobody is going to sort out the tooling for you,
either you're responsible for making sure all tooling (not just your personal
IDE) works with your funky name or it'll get normalised to ASCII.

Examples of places to use ASCII:
* domain names
* URL paths / endpoint names
* table names and column names
* directory / file names
* git repositories
* docker image / container names
* AWS resource names
* anywhere else where there's no schema/metadata where UTF-8 would logically
  be specified

By design, the first 128 characters of Unicode (when encoded as UTF-8) are 
exactly binary-compatible with US-ASCII. That means you can safely store ASCII 
in a UTF-8 field with without concern.  Though if some other process stores
non-ASCII in a UTF-8 field and you try to read it into an ASCII context,
you will get one of three likely outcomes: 
* a conversion error 
* an ASCII string with the ASCII character `?` used as a 
[replacement character](https://www.fileformat.info/info/unicode/char/fffd/index.htm) 
for each non-ASCII character (Unicode standard behaviour for converting
unrepresentable characters).
* an ASCII string containing 
[mojibake](https://en.wikipedia.org/wiki/Mojibake)


## Use UTC everywhere

Any time you serialise a date: across the wire, into a html attribute, to disk,
to a local store, to a DB column.

On the rare occasion that you have actual, documented business need for
storing a Time Zone, consider modelling the TZ separately (yes, literally -
as a separate column, e.g. "local_client_entry_tz", "required_target_tz", etc.)
Make it explicitly clear which TZ it stores and why you care about that TZ so
much  - if you care about the TZ for some reason, I guarantee there's at least
two actual timezones involved that you should be storing.

The rest of the time, push TZ stuff as high as possible - above the
business/application logic, all the way into into the
presentation/view/controller logic (if that's how you roll).

Postgres columns should _generally_ be modelled as timestamp without time zone.
Yes, I'm aware of the Postgres 
["don't do this"](https://wiki.postgresql.org/wiki/Don't_Do_This#Don.27t_use_timestamp_.28without_time_zone.29) 
advice. Raido falls squarely in the "When should you?" camp.

All APIs values should be UTC.

All log timestamps should be UTC.

All database engines timezone should be configured UTC +0.

The Operating System timezone for all non-client machines should be configured
to UTC +0.

Software VM components (JDK, Node.js, Spring, JDBC internals, JDBC driver) -
should be configured to UTC +0 explicitly; in code if you have to otherwise 
via config or startup params. Note that if you set the JVM timezone
programmatically, it doesn't propagate to the JDBC driver (at least - probably
other weirdness too).

Note that most docker containers run in VMs (Windows docker desktop setups)
will have a TZ of UTC. For example, if you run a postgres container, the
postgres instance will likely end up with UTC as the timezone, regardless
of the timezone settings of your OS.

It's fine to set your local AWS console, log viewer etc. to a timezone,
that's where TZ handling is supposed to be done - on the client, with a
sensible local default, under the control of the user.

This won't solve all our Timezone issues - but it'll gets us surprisingly far,
and provides a significant amount of predictability along the way.


## Use Unix file endings

Everywhere else we have a need to store or generate files (e.g. statically
generated server files, text area content, etc.) - use unix line endings.

Windows developers: learn to use tools that can deal with that, or make sure
your software supports both (and defaults to unix for anything that gets
committed, transferred to a server, etc.)

Leo wins the inaugural "someone actually reviewed the code standards" award.
The prize is this emoji - 🦁.
Whenever someone demonstrates actually reading the documentation, the 
Leo the Lion emoji will be awarded in slack.

Most repos should have a `.gitattributes` file with `text=lf`.

### Why not "auto"?

* Docker images
  * don't want to be copying CR/LF files into the Docker images.
    Some Linux utilities will choke on them.
* XML files (and many other types of resource file)
  * don't want the (multiline) strings going through code to differ depending
    on the OS used to process them
* When calculating the hash on a file, we want it to be the same on any OS
  * example: depending on the tool/config used, lambda code built on different
    platforms ends up with different hash codes causing unnecessary deployments

Examples of current, concrete line ending failures in the repo:
* JOOQ/Flyway generates platform specific line endings in DB comments, so if a 
  linux and windows user both migrate and generate code for the same 
  schema, they git a bunch of diffs around 
  line endings 
    * it's only the line endings in things like column comments, so the 
      problem is likely actually Flyway rather than JOOQ 
* CDK
  * "assets" in CDK seem to get generated with line endings in them, so when
    running on Windows, you will always see these show in a "cdk diff"

If you do need cr/lf endings, say you want to implement a user function for 
downloading a CSV file that looks nice in Windows notepad. Push that code all 
the way into the last possible layer where you detect the client platform 
automatically and apply the line endings at the last moment.

Failing that, make a optional parameter, defaulting to unix line endings,
then still try and push the decision up onto the client were you can change
the default based on the platform. But then you'll probably end up needing
a UI control anyway so that Windows users can generate files with Unix line
endings, because reasons. Seriously, you can't win. All you can do is
have a sensible default so both developers and users can predict what your 
system will probably do, most of the time.


## Always explain deprecations 

When marking an API endpoint or data structure deprecated - always provide some 
explanation of what the intended upgrade path is (i.e. what they should do 
instead). Don't just mark stuff deprecated. 

This goes double for when you're intentionally sunsetting actual functionality, 
not just the API. Don't make customers figure that out on their own.



