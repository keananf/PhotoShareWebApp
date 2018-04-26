# PhotoShare
Client and RESTful server built with Jersey and Grizzly 

## Overview
This system is split into 2 components: a server using gradle, and a web-based client. 

### Building the server
Simply navigate to the project's top-level directory and run the following command: 
./gradlew build

Please note, a more recent version of gradle may be needed when building. If so, run the following commands.
- ./gradlew wrapper --gradle-version 4.7
- ./gradlew --version

### Running Server and Web-Client
Simply navigate to the project's top-level directory and run the following command: 
./gradlew run

With the server running, the client will be exposed at the following URL: `http://localhost:8080`
The client can also then be accessed from remote machines as well.

Note: Many web-clients can be running at a time.
Note: Only one ServerMain instance can be running at a time.
