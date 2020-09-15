import java.util.ArrayList; //used to implement array lists
import java.util.Random; //used to get random decisions

/**
 * Class extends from Player class but has more particular methods.
 * It represents a bot player which looks for the player (randomly)
 * and when found, it chases the player using a path finding algorithm.
 */
public class BotPlayer extends Player {

    private ArrayList<char[]> memoryMap; // 5*5 grid shown when calling look command
    private int Decision = 1; // counter to alternate between moving and looking
    private boolean PlayerFound = false;
    private int playerRow;
    private int playerColumn;

    /**
     * Constructor which calls the super() method
     * from its parent class.
     */
    public BotPlayer() {
        super();
    }

    /**
     * Sets the value of the boolean variable to false
     * to signal that the player is no longer in the 5*5 grid
     * obtained when calling look.
     */
    public void lostPlayer(){
        PlayerFound = false;
    }

    /**
     * Sets the value of the boolean variable to true,
     * to signal that the player is in reach and updates
     * its coordinates from the 5*5 grid
     * @param row row on which the bot sees the player when
                    calling the look command
     * @param column column on which the bot sees the player
                    when calling the look command
     */
    public void setPlayerFound(int row, int column){
        PlayerFound = true;
        playerRow = row;
        playerColumn = column;

    }

    /**
     * Updates the memoryMap variable with the 5*5 grid obtained
     * when the bot calls the look command.
     * @param visibleMap 5*5 grid which is shown to the bot
                        when calling the look command
     */
    public void updateMemoryMap(ArrayList<char[]> visibleMap){
        memoryMap = visibleMap;
    }

    /**
     * Based on the values of its fields, the bot makes a decision of whether
     * to call the "look" command or a "move" command*/
    public String makeDecision(){
        /*
        * Basic strategy is that after every "move" command, the bot calls a "look" command.
        * Also, first command is always "look".
        */
        if(Decision % 2 == 1){
            Decision++;
            return "LOOK";
        }
        else{
            Decision++;
            if(PlayerFound){
                return pathToPlayer();
            }
            else{
                return randomMovement();
            }
        }
    }

    /**
     * Makes use of Lee's algorithm for shortest path finding, implemented in the Lee class
     * to find the shortest path to the player's position.
     * @return the first move the bot has to make, from this shortest path
     */
    private String pathToPlayer()
    {
        /*
         * coordinates which will be updated, as we backtrack our way
         * from target to start
         */
        int leeRow = playerRow;
        int leeColumn = playerColumn;
        int[][] leeMap; // map which contains the minimum number of moves from the center to each point
        /*
         * create a Lee object, which will generate
         * the map of minimum steps to each point from the centre
         * based on grid obtained from the "look" command
         */
        Lee lee = new Lee(memoryMap);
        leeMap = lee.getLeeMap();

        // Player is in an unreachable position (only apparently)
        if(leeMap[leeRow][leeColumn] == 26){
            return randomMovement();
        }

        // Keep backtracking from the target, until we are 1 step away from the source
        while(leeMap[leeRow][leeColumn] != 1){
            // Check for map limits
            if(leeRow > 0){
                if(leeMap[leeRow - 1][leeColumn] < leeMap[leeRow][leeColumn]){
                    leeRow -= 1;
                    continue;
                }
            }
            if(leeColumn > 0){
                if(leeMap[leeRow][leeColumn - 1] < leeMap[leeRow][leeColumn]){
                    leeColumn -= 1;
                    continue;
                }
            }
            if(leeRow < 4){
                if(leeMap[leeRow + 1][leeColumn] < leeMap[leeRow][leeColumn]){
                    leeRow += 1;
                    continue;
                }
            }
            if(leeColumn < 4){
                if(leeMap[leeRow][leeColumn + 1] < leeMap[leeRow][leeColumn]){
                    leeColumn += 1;
                }
            }
        }

        // Being 1 step away from the source, we are in one of th 4 possible adjacent positions
        if(leeRow == 2){
            if(leeColumn == 3){
                return "move e";
            }
            if(leeColumn == 1){
                return "move w";
            }
        }
        if(leeColumn == 2){
            if(leeRow == 3){
                return "move s";
            }
            if(leeRow == 1){
                return "move n";
            }
        }

        return randomMovement(); // if anything went wrong along the way, end the function with a random movement
    }

    /**
     * By generating a pseudo-random number, a random movement is being chosen.
     * Until we find a valid move, we will keep generating random decisions.
     * The bot will use its knowledge from the previous "look" command to
     * make sure that its decision will always be a success.
     * @return the command "move " + the direction*/
    private String randomMovement(){
        while(true){
            String decision = "move ";
            int direction = new Random().nextInt(1000)%4;
            char mapCharacter = ' ';
            // get the tile character on which the bot would land, based on its "random" decision
            switch (direction){
                case 0: mapCharacter = memoryMap.get(2)[3];
                            decision = decision.replace("move ", "move e"); break;
                case 1: mapCharacter = memoryMap.get(2)[1];
                            decision = decision.replace("move ", "move w"); break;
                case 2: mapCharacter = memoryMap.get(1)[2];
                            decision = decision.replace("move ", "move n"); break;
                case 3: mapCharacter = memoryMap.get(3)[2];
                            decision = decision.replace("move ", "move s"); break;
            }
            // check if it is valid or not
            if(mapCharacter != '#'){
                return decision;
            }
        }
    }
}
