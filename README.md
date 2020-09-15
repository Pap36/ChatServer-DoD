# ChatServer-DoD

Implementation of a Chat Server in Java using threads. 

# Main functionalities: 
- real time messaging between users
- very simplistic Bot which broadcasts a sample message to all users
- users can locally play Dungeons of Doom, a turn based command line game

# How to run the server:
 1. After compiling all files, run "java ChatServer" to start the server
    Note that the default port of the server is 14001 and the IP address is "localhost"
    If you want to specify a different port, use -ccp as a command line argument
 2. To connect as a user, run "java ChatClient"
 3. To connect the bot to the chat, run "java ChatBot"
 4. Note that for both the client and the bot, the default IP address used when connecting to the server
    is "localhost" and the port is 14001. To change the IP address, use -cca as a command line argument and
    to change the port, use -ccp.
 
# In chat commands:
- type "hey Quacky" to recieve the sample message from the bot
- type "/disconnect" to disconnect from the server
- type "/play DoD" to play the game locally

To close the server, type "EXIT" in the server's terminal.

# To see the rules of Dungeons of Doom, read the DoD_README.txt file
