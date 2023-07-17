At the moment, Raido mints handles for the CNRI 
[Global Handle Registry](https://www.handle.net/) 
indirectly through the ARDC's 
[APIDS service](https://github.com/au-research/ANDS-PIDS-Service).

[ApidsService.java](../spring/src/main/java/raido/apisvc/service/apids/ApidsService.java)
encapsulates the communication with APIDS.  Note that APIDS has limited 
functionality, but it supports the current minimal requirements of Raido.

Other RAID registration agencies will not be able to use the ARDC APIDS 
service. 
In order for registration agencies to use the Raido codebase, they will have
to write their own handle minting implementation.  When that happens we should
analyse the usage and implement a shared interface that can be configured
with the correct implmentation at startup time (either a a customisation 
property for 
[ApiConfig.java](../spring/src/main/java/raido/apisvc/spring/config/ApiConfig.java)
or perhaps each registration agency could inherit from ApiConfig to customise
their own setup (RaidoApiConfig, SurfApiConfig, etc.)
