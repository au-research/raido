### Use LTS Node.js 16

* Status: proposed                                     
* Who:  proposed by STO                               
* When: proposed on 2022-07-21
* Related: none 


# Decision

Use Node.js LTS v16 by default.

Use this everywhere: in aws repo for CDK code and lambdas, in app-client for 
react build, EC2 instances, inside container, on dev machines.

Might want to consider using 18 though, since it should go LTS approx. 
OCT 2022.  But stuff might not support that yet (lambda, CDK, etc?)


# Context

Ideally, we want same version across all where possible.

But that can be tricky balancing AWS Lambda support, AWS CDK support, 
create-react-app support, etc.  May have to use different versions for
specific things - document as ADR in the project and the local readme.


# Consequences
 

# Links


