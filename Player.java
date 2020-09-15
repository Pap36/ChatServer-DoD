/**
 * class which represents the Player with its basic methods
 */
public class Player {

    private int currentGold;
    private int row;
    private int column;
    private char currentTile;

    /**
     * Constructor which also sets the
     * player's current gold value to 0.
     */
    public Player (){
        currentGold = 0;
    }

    /**
     * Increases the gold value
     * the player has by 1.
     */
    public void addGold(){
        currentGold++;
    }

    /**
     * Sets the row value coordinate
     * on which the player is.
     * @param row row coordinate
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the column value coordinate
     * on which the player is.
     * @param column column coordinate
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Sets the tile on which the player
     * is currently sitting.
     * @param tile character from map on which player
                    is currently sitting
     */
    public void setCurrentTile(char tile){
        currentTile = tile;
    }

    /**
     * Returns the gold value the player has.
     * @return gold value
     */
    public int getCurrentGold() {
        return currentGold;
    }

    /**
     * Returns the tile on which the player is.
     * @return character from map on which player
                is currently sitting
     */
    public char getCurrentTile(){
        return currentTile;
    }

    /**
     * Returns the row coordinate.
     * @return row coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column coordinate
     * @return column coordinate
     */
    public int getColumn() {
        return column;
    }
}
