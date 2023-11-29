Workbench
============

Overview
----------
Workbench is the front-end for the Breeding Management System developed by Leafnode (Efficio, DigitalLabs) for the Generation Challenge Programme.
This contains the UI that provides access to various tools and applications for the breeders (both native Windows and browser based apps).

### Sub-modules under Workbench contain the following:

1. Dashboard and Program Summary
2. Administration/Program Management tools
 - Program Locations
 - Program Methods
 - Program Creation and Deletion
 - Local Databases backup and restore
 - Custom User Tools (Alpha)
3. Statistical Analysis Tools frontend for Breeding View
 - Single Site Analysis
 - Multi Site Analysis
 - Meta Analysis for multi-variate
4. Workflows
 - Marker Assisted Recurring Selection (MARS)
 - Conventional Breeding (CB)
 - Marker Assisted Selection (MAS)
 - Marker Assisted Backcrossing (MABC)
5. Ontology Browser

Checkout the source code
-----------------------
The project is stored in the GIT repository hosted at github.com.  The URL for the repository is: 
[<pre>https://github.com/IntegratedBreedingPlatform/Workbench</pre>][workbench_git_link]

Build Dependencies to other projects
-------------------------------
Workbench is dependent on Middleware and Commons. See [Commons][commons_git_link] and [Middleware][middleware_git_link] for build instructions on those modules.

Prerequisites
----------------
For successful integration with the native tools and the apps, you need to have the crop databases from [DBScripts][dbscripts_git_link].

To Build
----------
You need Maven to build the project.

Configuration files are found in [BMSConfig][bmsconfig_git_link]. You will need to set up your own configuration files in this project.

To build using a specific configuration, run the following:

	mvn clean install -DenvConfig=dev-config-dir -DskipTests  
 
where `dev-config-dir` is the configuration specific to your build.

`-DskipTests` is optional, if you want to run unit-tests see [To Run Tests](#to-run-tests) section.

To Run Tests
--------------
To run JUnit tests using the command line, issue the following commands in the top level directory:

1. To run all tests: <pre>mvn clean test</pre>
2. To run a specific test class: <pre>mvn clean test -Dtest=TestClassName</pre>
3. To run a specific test function: <pre>mvn clean test -Dtest=TestClassName#testFunctionName</pre>

You need to specify the database to connect to in your BMSConfig files. 

All JUnit test suites require the rice database, except for `GenotypicDataManager` that uses the groundnut crop in testing.

Similar to building `Workbench`, add the `-DenvConfig` parameter to use a specific configuration.

To run JUnit tests using Eclipse, right-click on the specific JUnit test suite the `Workbench` project, select __Run As --> JUnit test__.

Developer guide and IDE setup
-------------------
See the [Setting Up Development Environment](https://github.com/IntegratedBreedingPlatform/Documentation/wiki/Setting-Up-Development-Environment) documentation.

For information on front end development in Workbench, see the `README.md` in `src/main/web`.

[commons_git_link]: https://github.com/IntegratedBreedingPlatform/Commons
[middleware_git_link]: https://github.com/IntegratedBreedingPlatform/Middleware
[workbench_git_link]: https://github.com/IntegratedBreedingPlatform/Workbench
[dbscripts_git_link]: https://github.com/IntegratedBreedingPlatform/DBScripts
[bmsconfig_git_link]: https://github.com/IntegratedBreedingPlatform/BMSConfig-v25
