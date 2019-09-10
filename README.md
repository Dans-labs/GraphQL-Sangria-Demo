graphql-sangria-demo
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/graphql-sangria-demo.png?branch=master)](https://travis-ci.org/DANS-KNAW/graphql-sangria-demo)

<!-- Remove this comment and extend the descriptions below -->


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

    graphql-sangria-demo -o value


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

        git clone https://github.com/DANS-KNAW/graphql-sangria-demo.git
        cd graphql-sangria-demo
        mvn install
