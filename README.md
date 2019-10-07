graphql-sangria-demo
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/graphql-sangria-demo.png?branch=master)](https://travis-ci.org/DANS-KNAW/graphql-sangria-demo)

SYNOPSIS
--------

    graphql-sangria-demo run-service


DESCRIPTION
-----------

A GraphQL demo implemented with Scala and Sangria


ARGUMENTS
---------

    Options:

       -h, --help      Show help message
       -v, --version   Show version of this program

    Subcommand: run-service - Starts GRAPHQL Sangria Demo as a daemon that services HTTP requests
       -h, --help   Show help message
    ---

EXAMPLES
--------

    graphql-sangria-demo run-service


GRAPHQL INTERFACE
-----------------

1. build `graphql-sangria-demo` using `mvn clean install`
2. make sure the [dans-dev-tools](https://github.com/DANS-KNAW/dans-dev-tools) are installed properly
3. call `run-reset-env.sh` from the root of the project
4. call `run-service.sh` from the root of the project
5. in your browser, go to http://localhost:20200/graphiql


GRAPHIQL TOOLS
--------------

To interact with the GraphQL servlet, use the internal http://localhost:20200/graphiql interface.
Alternatively, on Mac, use the [GraphiQL.app](https://github.com/skevy/graphiql-app).


GRAPHQL SCHEMA
--------------

To generate the latest version of the GraphQL schema for `graphql-sangria-demo`:

    #install get-graphql-schema
    npm install -g get-graphql-schema
    
    # (re)start the service (after: mvn clean install -DskipTests=true): see GRAPHQL INTERFACE above
    
    get-graphql-schema http://<base-url>/graphql > docs/schema.graphql


INSTALLATION AND CONFIGURATION
------------------------------


1. Unzip the tarball to a directory of your choice, typically `/usr/local/`
2. A new directory called graphql-sangria-demo-<version> will be created
3. Add the command script to your `PATH` environment variable by creating a symbolic link to it from a directory that is
   on the path, e.g. 
   
        ln -s /usr/local/graphql-sangria-demo-<version>/bin/graphql-sangria-demo /usr/bin



General configuration settings can be set in `cfg/application.properties` and logging can be configured
in `cfg/logback.xml`. The available settings are explained in comments in aforementioned files.


BUILDING FROM SOURCE
--------------------

Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher

Steps:

        git clone https://github.com/Dans-labs/GraphQL-Sangria-Demo.git
        cd graphql-sangria-demo
        mvn install
