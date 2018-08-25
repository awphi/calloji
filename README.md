# Calloji 
The goal is to create an online multiplayer world with a robust foundation allowing rapid content implementation.

* Establishing a robust server->client & client->server link that allows developers to rapidly implement custom packets to sync between server and client
* Secure the data sent between the server and client
* Validate all packets properly on the server-side to verify the client's actions as legitimate
* Unit testing each packet and it's handlers
* Implementation of an OpenGL renderer (likely using LWJGL) to display data sent to the client
* Creation of a map editor that can use the server side resources to create a savable map image
* Rapid addition of content to the game itself

## Logger levels guide:
* SEVERE (default level for client)
* WARNING (default level for server)
* INFO
* CONFIG
* FINE (--debug level for server and client)
* FINER
* FINEST (--stacktrace level for server and client)