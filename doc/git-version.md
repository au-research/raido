Note: We intended to stop using git-version.  Making the build reliant on the
presence of a full git installation and cloned repo is awkward for more modern
CI/CD approaches where the source tends to be managed as an artifact separate
from the process of cloning/retrieving the latest version.

 
* current HEAD is a commit with tag `version@1.2.3`, no uncommitted changes
* version = 1.2.3
* current HEAD is two commits descended from the above commit,
  with no other tags present, no uncommitted changes
    * version = 1.2.3-2-g179bd0e
* current HEAD is same as above, but with local uncommited changes
    * version = 1.2.3-2-g179bd0e.dirty
* `1.2.3-2-g179bd0e.dirty`
    * `1.2.3` - is from the tag, minus "version@"
    * `-` - dashes are to separate version number from commit distance and id
    * `2' - commit distance of HEAD from the tag, using "--first-parent"
    * `g179bd0e` - short git commit id, prefixed by `g` (which stands for "Git")
    * `.dirty` - standard suffix for any commit that has uncommitted changes
      </editor-fold> */
