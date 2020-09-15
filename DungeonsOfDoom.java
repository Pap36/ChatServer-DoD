import java.io.File; // file manipulation
import java.util.ArrayList; // using array list
import java.util.InputMismatchException; // file manipulation exception handling
import java.util.Scanner; // user input

/**
 * Game class which initializes the Map, Game Logic and plays the game
 */
public class DungeonsOfDoom {

    //Declaring fields
    private static ArrayList<String> filePathList; // array list of all the map files' names
    private static Map map; // game map
    // directory which contains all the maps
    private static final File directory = new File(DungeonsOfDoom.class.getProtectionDomain().
                                            getCodeSource().getLocation().getPath());
    private static final File[] files = directory.listFiles();
    private int mapNumber;

    /**
     * Constructor which initializes the array list of map files' names and starts the game
     * @param player ChatClient object representing the user who plays the game
     */
    public DungeonsOfDoom(ChatClient player) {
        filePathList = new ArrayList<>();
        initializeFilePath();
        printWelcomeMessage();
        loadMap();
        GameLogic game = new GameLogic(map, player);
        game.play();
    }
    /**
     * Create the array list of map files's names
     */
    private void initializeFilePath(){
        if(files != null){
            for(File file: files){
                // be sure to get just the .txt files
                if(file.getName().contains(".txt")){
                    filePathList.add(file.getName());
                }
            }
        }
    }
    /**
     * Print the name of each map file, without the extension
     */
    private void printMaps(){
        int index = 1;
        for(File file: files){
            if(file.getName().contains(".txt")){
                System.out.println(index + " --- " +
                        file.getName().substring(0, file.getName().length() - 4));
                index++;
            }
        }
    }
    /**
     * Welcomes the player to the game, and shows all the existing maps.
     * Asks the player to select one.
     */
    private void printWelcomeMessage(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Dungeons Of Doom");
        System.out.println("Please choose a map from the following:");
        printMaps();
        System.out.println("Please enter the number corresponding to the map you want to play.");
        // Check that an integer from 1 to the maximum number is entered
        try{
            mapNumber = scanner.nextInt() - 1;
            if(mapNumber < 0 || mapNumber > filePathList.size() - 1){
                invalidDifficultyMessage();
            }
        }
        catch (InputMismatchException e) {
            invalidDifficultyMessage();
        }
    }
    /**
     * Let the player know what happened because of an invalid map number
     */
    private void invalidDifficultyMessage(){
        mapNumber = -1;
        System.out.println("Input not valid. The default map is easyMap.\n" +
                "If you want to play the game with another mapNumber exit the " +
                "current session and restart the game.");
    }
    /**
     * Instantiate the correspondent Map object
     */
    private void loadMap(){
        if(mapNumber == -1){
            // default map
            map = new Map("easyMap.txt");
        }
        else{
            map = new Map(filePathList.get(mapNumber));
        }
    }
}