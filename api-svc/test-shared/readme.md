This project contains code that we want to share across various  
api-svc testing contexts:
like:
* unit tests
* integration tests
* load tests

This project should only be added to the class-paths of test code - don't add 
it as a dependency for production code.

I was pushing the concept of test-suites (i.e. `intTest`), but I couldn't
even figure out how to shared code between unit and integration tests.

Now that we're doing load testing as well (which is it's own project anyway
because of the Gradle plugin) - it's time to just have a project dedicated to 
sharing test utility code. 

