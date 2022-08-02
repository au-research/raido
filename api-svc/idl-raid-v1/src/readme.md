`raid-v1-1-.yaml` is copied from the original stored in the 
[doc](../doc/readme.md) area and then manually chopped down to be only what
Raido needs to implement to keep the UQ@RDM API calls working (nobody else
seems to use the API). 

As well as removing unwanted endpoints, the model names were normalised from
`RAiD` to `Raid` - because that casing is wildly annoying in code.

Also planning to remove unused model definitions at some point.