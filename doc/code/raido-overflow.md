Any time you get stuck for _more than a few minutes_ 
trying to figure out a _strange error message_, _multiple times_ - you should
add it to this list.

Especially where the developer isn't doing anything wrong and the problem is 
caused by inconsistent or unexpected behaviour of a tool or library.

Example: the "no constraint matching on conflict" error, I spent ages figuring
out that it wasn't something I was doing wrong.


# SQL error: `there is no unique or exclusion constraint matching the ON CONFLICT specification`

* Context: using on conflict` clauses on a partial indexes
* Workaround: wrap the partial condition value in `DSL.inline()`
* Solution: upgrade to jOOQ 3.18+
* see https://stackoverflow.com/a/73782610/924597
