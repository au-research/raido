This is a high-level view of the architecture.
The diagram is a a mix of "current state" and "future state" as at 2023-02-06.

The Raido stuff exists and is "current state".
The Raid stuff does not exist yet; in the medium to long term, the components
of the "Raid system" will likely be managed in a separate repository to this
one. 


# Architecture
<!--- Note the `?cache=no` param --->
![PlantUML model](https://www.plantuml.com/plantuml/svg/BOr12a8n34Jtda8Fq0R4bQj_bPW66sZJJvhezMt1rS2y6Tun7SsZjyjDb5eK3clNwdxE9u3XDx_5QxnYij5GP_LCemD6CfwCHdMjf8pqkKD7KIY3ODVYJ1x6VxpF2xCbUkHp2m_Mil87?cache=no)

Major architectural decisions are documented in the Architecture Decision Log:
* [high level](../adr)
* [api-svc](../../api-svc/doc/adr)
* [app-client](../../app-client/doc/adr)


# Technology

More info about individual technology choices can be found in the  
[technology-stack.md](../technology-stack.md).


----

# PlantUML technical information  


The diagram above is a 
[C4 container diagram](https://en.wikipedia.org/wiki/C4_model), 
generated using [PlantUML](https://plantuml.com/).
The source for this diagram is at
[raido-container-c4.puml](./raido-container-c4.puml).
It is dynamically generated (from the `main` branch), via the plantuml.com 
public web service, see this 
[StackOverflow example](https://stackoverflow.com/a/32771815/924597).

Note that we are only just learning about C4 modelling and the diagram above
likely mis-uses the C4 constructs - this diagram is very much a work in 
progress.

Maintainers: there's a decent 
[PlantUML plugin](https://plugins.jetbrains.com/plugin/7017-plantuml-integration) 
to help with writing diagrams, 
note that it is not maintained by JetBrains.
Also, I think non-Windows users may have to install PlantUML locally.
