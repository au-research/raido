### Use LTS JDK 17, Amazon Corretto

* Status: final                                     
* Who:  finalised by STO                               
* When: finalised on 2022-07-21
* Related: none 


# Decision

Use JDK 17, Amazon Corretto distribution by default.

Generally, swap to the latest LTS release when it's appropriate and update
documentation.

Use this everywhere by default.
On EC2 instances, inside containers, on dev machines.

Devs can use a different JDK distro (oracle, openjdk, etc.) on their  
machine if they want.  But if it causes hassles, they need to fix the code 
so it's compatible with the Corretto distro.


# Context

We have no backwards compatibility requirements - no reason not start with
latest LTS.

Non-LTS versions are not appropriate for Raido.


# Consequences

* old libraries may not support JDK 17
* registration agencies that want to re-use Raido code will need to  
  support the JDK version that Raido is implemented and tested with
 

# Links

* https://aws.amazon.com/corretto/

