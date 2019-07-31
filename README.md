# Calloji

## Introduction
Calloji was originally the working title for a vague online game of sorts. I wasn't entirely sure what I was going for hence why it failed very quickly. I took the remnants of the basic server/client connection code and built a more generic packet system around it.  

My friends and I had, in the past, always wanted to play a quick, no hassle game of online monopoly with each other on the PC. However, no such product existed. Calloji attempts to solve this problem with a quick pick up and play experience available on all OSes.

## Rules
> Based on [Monopoly Official Rules](https://en.wikibooks.org/wiki/Monopoly/Official_Rules)  

### Calloji Unique Rules
1. Hotels are now the combination of 5 houses, automatically combined when a 5th one is built.
2. There are infinite houses & hotels in the bank's possession so __there is no housing shortage rule__.
3. Players may auction their property at any time during their turn.
4. Mortgaged properties can be traded but not auctioned.
5. Rent calculations have been simplified slightly. They are now based on standard multipliers & not arbitrary values but they remain close to the original values.
6. Get out of jail free cards are tradable but will be used automatically upon entering jail. This is to stop purposeful game-stalling by waiting in jail to avoid rent.
7. There is a maximum of 1 hotel on each plot.

## Technical Information
This Calloji repository includes three modules (managed with maven). These are:

* Server
* Client
* __Sync__

All three modules are written in Java 8. However, Calloji uses raw TCP sockets to send and receive it's packets in standard JSON format. This means that, while a web client is infeasible, desktop clients in any language could be developed with relative ease.  

The sync module is particularly important as it contains the basic objects of a monopoly game available to both the client and the server. On top of this it also contains several utils used in both of the other modules but most importantly it has __the packet IDs__. This are vital in developing a new calloji client and thus I would advise creating a workflow to copy this enum into the language of your choosing as packets may be added/removed in future updates to the standard Calloji release. 

__NOTE__: Sent packets should always be followed by a new line character _(\n)_. Otherwise, you run the risk of packets piling up on the server and some being lost.
