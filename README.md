# PhotoShare
Client and RESTful server built with Jersey and Grizzly 

## Overview
This system is split into 3 components: a client, server, and a common library shared between them. All of them are gradle projects.

### Building all of the projects
Simply navigate to the project's top-level directory and run the following command: 
./gradlew build

In ConsoleClient and Server, this command will build the projects, dependencies included. 
For 'common,' the project is compiled into a jar, which is output in the build/libs directory. This can then be copied directly into the client/ and server/ project directories.

Note: If building the 'common' library, please ensure the gradle wrapper is at its latest version. To do this, type the following commands while in the common/ project directory:
./gradlew wrapper --gradle-version 4.2.1
./gradlew --version

### Running ConsoleClient / Server
Simply navigate to the project's top-level directory and run the following command: 
./gradlew run

Note: Many ConsoleClient instances can be running at a time.
Note: Only one ServerMain instance can be running at a time.

#### ConsoleClient input commands:
 - Adding user: --adduser [name] [password]
 - Logging-in user: --login [name] [password]
 - Getting all users: --getusers
 - Get all user's notifications: --getnotifications
 - Uploading picture: --upload [photoName] [pic.png]
 - Get a User's photos: --getphotos [userName]
 - Get Photo by Id: --getphoto [id]
 - Add comment to photo: --commentphoto [photoId] [message] 
 - Add comment to photo: --reply [commentId] [message]
 - Vote on a comment: --vote [commentId] ['true' or 'false']
 - Get all user's comments --getcomments [userName]
 - Get top-level comments for a photo: --getphotocomments [photoId]
 - Get top-level replies for a comment: --getreplies [commentId]
 - Admin API for removing a comment: --removecomment [commentId]

