IBPWorkbench
============

Overview
----------
IBPWorkbench is the forntend for the Breeding Management System developed by Efficio (Digitallabs) for the Generation Challenge program.
This contains the UI thats provides access to various tools and applications (both native Windows and browser based apps).

### Submodules under IBPWorkbench contains the following:
1. Dashboard and Program Summary
2. Administration/Program Management tools
- Program Locations
- Program Methods
- Program Creation and Deletion
- Local Databases backup and restore
- Custom Tools (Alpha)
3. Statistical Analysis Tools frontend for Breeding View
- Single Site Analysis
- Multi Site Analysis
- Meta Analysis for multi-variate
4. Workflows
- Marker Assisted Recurring Selection (MARS)
- Conventional Breeding (CB)
- Marker Assisted Selection (MAS)
- Marker Assisted Backcrossing (MABC)

Checkout this project
-----------------------
The project is stored in the GIT repository hosted at github.com.  The URL for the repository is: 
<pre>
    https://github.com/digitalabs/IBPCommons   
</pre>
An anonymous account may be used to checkout the project.  

No username and password is required.  You can also browse the content of the repository using the same URL.  

Dependencies to other projects
-------------------------------

IBPWorkbench is dependent on Middleware and IBPCommons. See [IBPCommons][ibpcommons_git_link], [IBPMiddleware][ibpmiddleware_git_link] for build instructions on those modules.

To Build
----------
You need Maven to build the project.

Configuration files are found on [./pipeline/config][configuration_link] you can make the built-in configuration there as a template if you want to use a custom configuration for your build.

To build using a specific configuration, run the following:  
<pre>
    mvn clean package -DenvConfig=dev-config-dir -DskipTests  
</pre>  
 
where `dev-config-dir` is the configuration specific to your build.

The `package` option tells maven to create a packaged .war file you can drop in the IBPWorkflow installation tomcat web-apps directory 

`-DskipTests` is optional, if you want to run unit-tests see To Run Tests section.

To Run Tests
--------------
To run JUnit tests using the command line, issue the following commands in the IBPCommons directory:
  1.  To run all tests: <pre>mvn clean test</pre>
  2.  To run a specific test class: <pre>mvn clean test -Dtest=TestClassName</pre>
  3.  To run a specific test function: <pre>mvn clean test -Dtest=TestClassName#testFunctionName</pre>

You need to specify the IBDB database to connect to in the testDatabaseConfig.properties file. 

All JUnit test suites require the rice database, except for GenotypicDataManager that uses the groundnut crop in testing.

Similar to building IBPWorkbecnh, add the -DenvConfig parameter to use a specific configuration.

To run JUnit tests using Eclipse, right-click on the specific JUnit test suite in the IBPCommons project, select Run As --> JUnit test.

Developer guide and IDE setup
-------------------
The following are links to developer guides and eclipse setup guide. (Note the linked documents are old and needs updating but still relevant even for newer builds)
- [Developer Guide][dev_guide_link]
- [Eclipse Setup][dev_eclipse_link]

##Other relevant Workbench documentation can be found [here][wokbench_conf_link]

[ibpcommons_git_link]: https://github.com/digitalabs/IBPCommons
[ibpmiddleware_git_link]: https://github.com/digitalabs/IBPMiddleware
[configuration_link]: https://github.com/digitalabs/IBPWorkbench/tree/master/pipeline/config
[dev_guide_link]: http://confluence.efficio.us.com/x/FYAt
[dev_eclipse_link]: http://confluence.efficio.us.com/x/1IA2
[workbench_conf_link]: http://confluence.efficio.us.com/x/LIAt
