IMPROVE: add doco of AWS stuff too?  People can't see the private repo, but no
need for the doco to be private.

# Update changelog

Through development, we add to changelog if/when we're sure that the change 
is likely to go to prod with next release.

When ready to release:
* update the changelog for the release (e.g. change heading from "Not 
  released" to "1.1")"
* tag the repo with the next tag (e.g. in this case: raido-v-1.1)
* add the date we expect to release

For any change that was added to changelog but unexpectedly was not released 
(moved changes onto a feature branch because not ready, or just wholly reverted)
just move the changelog entry from the release version to new "Not released" 
section at the top.


To tag a new version, invoke:
`git tag --annotate raido-v-<version> <COMMITHASH> -m "manual tag"`
eg: `git tag --annotate raido-v-0.9 HEAD -m "manual tag"`

Dont' forget to make sure you push the tag to Github, something like:
`git push --follow-tags`  (I've never done this, I use IDEA).


To list tags: `git tag`

To fetch tags from origin: `git fetch --tags`

Delete a tag: `git tag -d raido-v-1.1`