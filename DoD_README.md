# Dungeons-Of-Doom

Java implementation of a turn based command line game

# Game Rules: 
The game is played on a grid. Each '.' is a free place where the player can move. The '#' represent places which are not available to the player (obstacles). The 'P' is the player and the 'B' is the bot which constantly chases the player. 'G' represents gold which can be picked up and 'E' represents an exit out of the dungeon.

Each map has a fixed amount of gold needed in order to exit the dungeon and win the game. The player loses if either the Bot catches them or if they exit the dungeon with less gold than required. (Note that it is possible for more Gold to exist in the dungeon than required to win)

The game is turn based. In other words, the player will make a move, then the bot makes a move and so on.

#Possible moves: 
LOOK - displays a 5X5 grid which represents the surroundings of the player. The player is always in the center of the grid. MOVE N/E/S/W - moves in the specified direction PICKUP - pickus up Gold if the player is currently on a Gold tile. QUIT - exits the game. If the player is on an exit tile and has more than the required gold, then the player wins.

When the game ends, 'WIN' or 'LOSE' is displayed in the command line letting the player know their result. After each move, 'SUCCESS' or 'FAIL' is printed letting the player know if their move was succsessfuly executed or not. Note that a wrongly inputted command, like 'LOK' instead of 'LOOK' causes the player to lose their turn. Also, the commands are not case sensitive.

# For information regarding the format of a map, read the DoD_Map_README.txt file.
