import java.io.IOException;
import java.util.ArrayList; // using array lists
import java.util.Random; // randomly spawning the player and the bot on the map

/**
 * Class contains the game's logic
 */
class GameLogic {

    private Player player;
    private BotPlayer bot;
    private int turn = 0; // counter to decide whose turn it is
    private Map map; // game map
    private ArrayList<char[]> navigationMap; // the game map which will be used in the game logic for computation
    private boolean gameEnded = false;
    private int goldToWin;
    private ChatClient chatPlayer; // the user who plays the game

    /**
     * Constructor which initializes the game's logic on a specified map for a specified user
     * @param map Map object representing the DoD map
     * @param chatPlayer ChatClient object which represents the user who plays the game
     */
    public GameLogic(Map map, ChatClient chatPlayer){
        this.map = map;
        this.chatPlayer = chatPlayer;
        this.goldToWin = map.getGold();
        this.navigationMap = map.getMap();
    }

    /**
     * Randomly spawn a Player object on the map.
     * @param player Player object representing either the human player or the bot
     */
    private void generateRandomCoordinates(Player player){
        Random random = new Random();
        int row = random.nextInt(map.getRows());
        int column = random.nextInt(map.getColumns());
        // depending on whether it is the human player or the bot, spawn them differently
        // keep trying to randomly spawn them until the Player object is placed on a valid tile
        if(player == this.player){
            while(navigationMap.get(row)[column] != '.' && navigationMap.get(row)[column] != 'E'){
                row = random.nextInt(map.getRows());
                column = random.nextInt(map.getColumns());
            }
        }
        else{
            while(navigationMap.get(row)[column] == 'P' || navigationMap.get(row)[column] == '#'){
                row = random.nextInt(map.getRows());
                column = random.nextInt(map.getColumns());
            }
        }
        // update the player's fields accordingly
        player.setCurrentTile(navigationMap.get(row)[column]);
        player.setColumn(column);
        player.setRow(row);
    }

    /**
     * Processes the command given by one of the Players.
     * @param command the command
     * @param player Player who gave the command
     */
    private void processCommand(String command, Player player){
        // checking if it is a move command first
        if(command.equals("move e") || command.equals("move w") ||
                command.equals("move s") || command.equals("move n")){
            MOVE(command.replace("move ", ""), player);
            return;
        }
        // checking for the other commands
        switch (command) {
            case "hello": HELLO(); return;
            case "gold": GOLD(); return;
            case "look": LOOK(player);return;
            case "pickup": PICKUP();return;
            case "quit": QUIT();return;
            default:
                // Command is not valid and the turn is wasted.
                System.out.println("Unrecognized command.\nFAIL.");
        }
    }

    /**
     * Look method. Analyzes the 5*5 grid surrounding the player.
     * If it is the human player, it displays it.
     * Else, the bot calls the updateMemoryMap() method to make use
     * of the grid for its next decision.
     * @param player Player object which can be either a human player or a bot
     */
    private void LOOK(Player player){
        boolean playerFound = false; // checking to see if the player is in sight
        ArrayList<char[]> visibleMap = new ArrayList<>(); // the 5*5 grid which will be passed to the bot if needed
        int visibleRowIndex;
        int row = player.getRow();
        int column = player.getColumn();
        // Player is always in the centre
        for(int rowIndex = row - 2; rowIndex <= row + 2; rowIndex++){
            // create arrays of characters for each row, and add them to the array list
            char[] visibleRow = new char[100];
            visibleRowIndex = 0;
            for(int columnIndex = column - 2; columnIndex <= column + 2; columnIndex++){
                // Visible area outside the map
                if(rowIndex < 0 || columnIndex < 0 ||
                        rowIndex >= map.getRows() || columnIndex >= map.getColumns()){
                    // checking which player called this method
                    if(player == this.player){
                        System.out.print('#');
                    }
                    else{
                        visibleRow[visibleRowIndex] = '#';
                        visibleRowIndex++;
                    }
                }
                // Visible area inside the map
                else {
                    if(player == this.player){
                        System.out.print(navigationMap.get(rowIndex)[columnIndex]);
                    }
                    else{
                        // if we found the player, update the value of the boolean variable and let the bot know
                        if(navigationMap.get(rowIndex)[columnIndex] == 'P'){
                            this.bot.setPlayerFound(rowIndex + 2 - row, columnIndex + 2 - column);
                            playerFound = true;
                        }
                        visibleRow[visibleRowIndex] = navigationMap.get(rowIndex)[columnIndex];
                        visibleRowIndex++;
                    }
                }
            }
            if(player == this.player){
                System.out.println();
            }
            if(player == this.bot){
                visibleMap.add(visibleRow);
            }
        }
        // if bot called method, pass the information further
        if(player != this.player){
            bot.updateMemoryMap(visibleMap);
        }
        // if bot called method and the player was not found, let the bot know
        if(!playerFound && player == this.bot){
            this.bot.lostPlayer();
        }
    }

    /**
     * Method which moves the player who calls it
     * @param direction direction in which the player wants to go
     * @param player Player object which can be either thr human or the bot
     */
    private void MOVE(String direction, Player player){
        int row = player.getRow();
        int column = player.getColumn();
        // Updating the coordinates based on the direction
        switch (direction){
            case "e": column += 1; break;
            case "w": column -= 1; break;
            case "n": row -= 1; break;
            case "s": row += 1; break;
        }
        // Checking that the position is valid
        char characterFromMap = navigationMap.get(row)[column];
        // bot caught the player
        if((characterFromMap == 'P' && player == this.bot) || (characterFromMap == 'B' && player == this.player)){
            this.player.setCurrentTile('B');
            this.bot.setCurrentTile('P');
            gameEnded = true;
            return;
        }
        if(characterFromMap != '#'){
            // assign the map 'P' or 'B' character the tile character on which the player was sitting before
            navigationMap.get(player.getRow())[player.getColumn()] = player.getCurrentTile();
            player.setCurrentTile(navigationMap.get(row)[column]); // get the new tile character
            player.setRow(row);
            player.setColumn(column);
            //update the map with either 'P' or 'B' characters
            if(player == this.player){
                navigationMap.get(row)[column] = 'P';
            }
            else {
                navigationMap.get(row)[column] = 'B';
            }
            // print message to let the player know if it succeeded or not
            if(player == this.player){
                System.out.println("SUCCESS");
            }
        }
        else {
            if(player == this.player){
                System.out.println("FAIL");
            }
        }
    }

    /**
     * Displays the gold required to win.
     */
    private void HELLO(){
        System.out.println("Gold to win: " + goldToWin);
    }

    /**
     * Display's the gold currently owned by the player
     */
    private void GOLD(){
        System.out.println("Gold owned: " + player.getCurrentGold());
    }

    /**
     * Attempts to pickup gold from the dungeon.
     */
    private void PICKUP(){
        if(player.getCurrentTile() == 'G'){
            player.addGold();
            /*
             * it is important to update the tile character on which
             * the player is sitting to '.' so we don't get an infinite gold bug
             */
            player.setCurrentTile('.');
            System.out.print("SUCCESS. ");
        }
        else{
            System.out.print("FAIL. ");
        }
        // display the gold owned
        GOLD();
    }

    /**
     * End the game
     */
    private void QUIT(){
        gameEnded = true;
    }

    /**
     * Execute the flow of the game:
     * Initialize and spawn the player and the bot.
     * Based on the turn, process each player's commands until the game has ended.
     */
    public void play(){
        // creating a list of the players and using the turn variable as index
        ArrayList<Player> players = new ArrayList<>();
        player = new Player();
        bot = new BotPlayer();
        players.add(player);
        players.add(bot);
        generateRandomCoordinates(player);
        navigationMap.get(player.getRow())[player.getColumn()] = 'P'; // update the map tile
        generateRandomCoordinates(bot);
        navigationMap.get(bot.getRow())[bot.getColumn()] = 'B'; // update the map tile
        String command = "";
        while (!gameEnded){
            // get all input commands to lower case
            if(players.get(turn) == player){
                try {
                    // commands are received by accessing the user's input stream from the keyboard
                    command = chatPlayer.getUserIn().readLine().toLowerCase();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            else{
                command = bot.makeDecision().toLowerCase();
            }
            processCommand(command, players.get(turn));
            turn++;
            turn %= 2;
        }
        // check if the player won or lost
        if(player.getCurrentTile() == 'E' && player.getCurrentGold() >= goldToWin && bot.getCurrentTile() != 'P'){
            System.out.println("WIN");
        }
        else {
            System.out.println("LOSE");
        }
    }
}
