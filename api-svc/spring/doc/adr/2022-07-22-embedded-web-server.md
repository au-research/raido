
### Use Jetty

* Status: final
* Who:  finalised by STO
* When: finalised on 2022-07-22
* Related: no related ADRs


# Decision

Use Jetty embedded web server.


## STO opinion

Tomcat was always more popular, but it was slower and had lots dodgy cruft in
it because of its status as the reference implementation for Servlets/JSP
standard.

https://web.archive.org/web/20211121070337/https://www.theserverside.com/video/Tomcat-vs-Jetty-How-these-application-servers-compare
* that article is from Nov 2019, reasonably recent
  * aligns with my personal views
  * we definitely don't care about the latest and greatest standard, 
  but do care about performance
  * the larger tomcat community is a definite concern
  * but I've always found answers to deep Jetty problems easier to find 
  because less "noise" - 99% of "technical" tomcat articles are clickbait, 
  content marketing or just entry-level fluff.

More recently though, if you google "tomcat+vs+jetty+performance",

* you're more likely to find articles like this:
  https://www.baeldung.com/spring-boot-servlet-containers
* where the difference is close enough that it doesn't really seem to matter 
  much.


# Context

Need to pick.  Spring-boot defaults to Tomcat.  Most people use Tomcat.
It's not too big a deal.  A bit of config to slot the server into the Spring
framework and most of the rest of the code is bound to Spring, not the web 
server. 


# Consequences

* searches for help dominated by tomcat results 




