import java.io.File; // library for manipulating files
import java.io.FileNotFoundException; // file manipulation exception handling
import java.net.URL; // obtaining the path of a certain file
import java.util.ArrayList; // used to implement array lists
import java.util.Scanner; // used to read files

/**
 * Class which represents the map of DoD
 */
public class Map {

    private int goldToWin;
    private ArrayList<String> map;
    private int rows, columns;

    /**
     * Constructor which initializes the rows and columns
     * variables with 0. It also loads the map using a filepath.
     * @param filePath path of .txt file which contains the DoD map
     */
    public Map(String filePath) {
        rows = 0;
        columns = 0;
        load(filePath);
    }

    /**
     * Loads the map from a specified path and
     * adds the map to the class variable map. It also
     * displays the name of the map.
     * @param filePath path of .txt file which contains the DoD map
     */
    private void load(String filePath){
        // getting the path of the file and creating a File variable which represents the .txt file
        URL path = DungeonsOfDoom.class.getResource(filePath);
        File mapTxtFile = new File(path.getFile());
        map = new ArrayList<>();
        // using try catch for reading the file
        try{
            Scanner sn = new Scanner(mapTxtFile);
            // display the name of the map (first line of the file, without the "name" tag)
            System.out.println(sn.nextLine().replace("name ", ""));
            // getting the amount of gold required to win (second line of the file, without the "win" tag)
            goldToWin = Integer.parseInt(sn.nextLine().replace("win ", ""));
            // reading the map
            while (sn.hasNextLine()){
                map.add(sn.nextLine());
                rows++;
            }
            columns = map.get(0).length();
            sn.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File not found sorry");
        }
    }

    /**
     * Returns the map as an array list of character arrays.
     * @return the array list of character arrays, which has been
                obtained by converting the original array list of
                Strings.
     */
    public ArrayList<char[]> getMap() {
        ArrayList<char[]>convertedMap = new ArrayList<>();
        for(String string : map){
            convertedMap.add(string.toCharArray());
        }
        return convertedMap;
    }

    /**
     * Returns the amount of gold the player must have
     * in order to win.
     * @return amount of gold which is necessary for winning
     */
    public int getGold() {
        return this.goldToWin;
    }

    /**
     * Returns the number of rows the map has.
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns the map has.
     * @return number of columns
     */
    public int getColumns() {
        return columns;
    }
}
