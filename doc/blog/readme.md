This directory contains "blog" style documentation where we document all sorts
of things about the raid system.

The plan is to publish each topic here as an `.md` file, if anyone wants to
publish the content somewhere else (RSS, Hapeo, ARD blog, twitter, etc.), they
can.


# Possible blog topics


## Failure modes of the project

Failure modes of the overall project:
* lack of up-take
* failure to realise the savings projected 
  * projections
    * [PID cost-benefit analysis](https://doi.org/10.5281/zenodo.7356219)
    (this is the UK one, can't find a public reference for the Aus version)
    * https://ardc.edu.au/article/how-much-are-persistent-identifiers-worth-to-australian-research/
  * the appearance of failure being just as important as actual failure
    * if we can't "show" (not "tell") the cost benefits of RAiD, then 
      eventually the funding will not be made available to continue and improve
* project derailment via scope-creep
  * there is a strong temptation for interested parties to use RAiD to avoid 
  implementing their own systems (research data management services, etc.)
  * if RAiD tries to be all things to all people - it will end up stuck in 
    analysis-paralysis, or collapse under it's own weight


## RAiD is not a research service

The API and service itself are an administrative support service, not a 
research tool.

Researchers should not use either the public or authenticated API endpoints to 
gather data for analysis in research projects.

Research and analysis should based on the publicly available, regular 
data-export archives that RAiD will publish (for all "open" raid data).
These public RAiD archives will have DOI references and supporting 
information to support modern data provenance practices for research.

If researchers want to perform analysis on "closed" or "embargoes" RAiD data,
they will need to approach each service-point individually.  RAiD will support
this by providing service-points with the ability to securely export all their 
data, including non-public data, in a format similar to the public data 
archives.  It will then be up to the service-point and research to agree and
co-ordinate privacy and technical issues related to the exported data. 