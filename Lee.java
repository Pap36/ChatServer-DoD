import java.util.ArrayList; // to use array lists

/**
 * Class represents a Lee object, which applies
 * the lee algorithm (shortest path finding) to the
 * 5*5 grid obtained from the bot "look" command.
 */
public class Lee {

    private ArrayList<Node> lastVisited; // array list of nodes, which have been visited
    private int[][] leeMap = new int[5][5]; // map with the least steps from the centre for any point
    /**
     * Class which is defined inside the Lee class, and represents
     * a single point in the 5*5 grid, defined by multiple fields.
     */
    private class Node{

        private ArrayList<Node> neighbours; // each point has neighbours, which are also Nodes
        private int distanceFromStart; // the distance from the centre to the point
        private int row ,column; // its coordinates on the 5*5 grid

        /**
         * Constructs the Node class and initializes the neighbours list,
         * its coordinates, and the default distance from the centre.
         * @param row row coordinate for the point
         * @param column column coordinate for the point
         */
        private Node(int row, int column){
            neighbours = new ArrayList<>();
            /*
             * maximum distance in a 5*5 is 25.
             * we use the distance of 26 as default to be able
             * to update it accordingly.*/
            distanceFromStart = 26;
            this.row = row;
            this.column = column;
        }

        /**
         * Returns the row coordinate of the point.
         * @return row coordinate
         */
        private int getRow(){
            return row;
        }

        /**
         * Returns the column coordinate of the point.
         * @return column coordinate
         */
        private int getColumn(){
            return column;
        }

        /**
         * Update the distance from the centre of the grid only if the one
         * which is being passed is strictly smaller than the current one.
         * @param distance computed distance from the centre
         * @return true if distance < current distance from the centre
                    false otherwise
         */
        private boolean updateDistanceFromStart(int distance){
            if(distance < this.distanceFromStart){
                this.distanceFromStart = distance;
                return true;
            }
            return false;
        }

        /**
         * Returns the current distance from the centre.*
         * @return the current distance from the centre
         */
        private int getDistanceFromStart(){
            return  distanceFromStart;
        }

        /**
         * Adds a neighbour to the neighbours list.
         * @param neighbour another Node object, which represents an
                           adjacent point on the grid with respect to this
                            point
         */
        private void addNeighbour(Node neighbour){
            neighbours.add(neighbour);
        }

        /**
         * Returns the array list of neighbours of this point.
         * @return array list of Node objects which represent the neighbours of this
                    point
         */
        private ArrayList<Node> getNeighbours(){
            return neighbours;
        }
    }

    /**
     * Class which represents all the free spaces on the grid
     * as an array list of Node objects.
     */
    private class Graph{

        private ArrayList<Node> nodes;
        // 2 arrays which make the transition from one point to its 4 neighbours easier
        private int[] rowDirection = {-1, 0, 1, 0};
        private int[] columnDirection = {0, 1, 0, -1};

        /**
         * Constructor which initializes and creates
         * the list of Node objects which represent free tiles from the
         * 5*5 gird.
         * @param map the 5*5 grid obtained through the "look" method
         */
        private Graph(ArrayList<char[]> map){
            nodes = new ArrayList<>();
            for(int row = 0; row < 5; row++){
                for(int column = 0; column < 5; column++){
                    //check if it is a free tile
                    if(map.get(row)[column] != '#'){
                        nodes.add(new Node(row, column));
                    }
                }
            }
        }

        /**
         * Gets passed the coordinates of a point and
         * returns that Node object, if it exists.
         * @param nodeRow row coordinate
         * @param nodeColumn column coordinate
         * @return Node object or null*/
        private Node findNode(int nodeRow, int nodeColumn){
            for(Node node: nodes){
                if(node.getRow() == nodeRow && node.getColumn() == nodeColumn){
                    return node;
                }
            }
            return null;
        }

        /**
         * Creates a link between the Node objects, based on neighbours.
         */
        private void linkNodes() {
            for (int row = 0; row < 5; row++) {
                for (int column = 0; column < 5; column++) {
                    // get the current point and check that it exists
                    Node currentNode = findNode(row, column);
                    if (currentNode != null) {
                        // go through each of its possible neighbours
                        for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
                            // check that a neighbour exists or not
                            Node neighbour = findNode(row + rowDirection[directionIndex],
                                    column + columnDirection[directionIndex]);
                            if (neighbour != null) {
                                // link the neighbour to the Node object.
                                currentNode.addNeighbour(neighbour);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Apply Lee's algorithm on the linked Node objects, from a starting point.
         * @param startingNode position from which the shortest path
                               to each point will be computed
         */
        private void lee(Node startingNode){
            // starting point is marked as visited
            lastVisited.add(startingNode);
            while(lastVisited.size() > 0){
                Node node = lastVisited.get(0);
                // going through each neighbour of the current Node object
                for(int neighbourIndex = 0; neighbourIndex < node.getNeighbours().size(); neighbourIndex++){
                    Node neighbour = node.getNeighbours().get(neighbourIndex);
                    /*
                     * update the distance, and if it is indeed updated,
                     * add the neighbour to the lastVisited list
                     */
                    if(neighbour.updateDistanceFromStart(node.getDistanceFromStart() + 1)){
                        lastVisited.add(neighbour);
                        // update the minimum distance from the starting point on the leeMap as well
                        leeMap[neighbour.getRow()][neighbour.getColumn()] = neighbour.getDistanceFromStart();
                    }
                }
                // remove the current Node object from the list and go to the next one
                lastVisited.remove(node);
            }
        }
    }

    /**
     * Constructor which initializes the leeMap variable,
     * creates the Graph object and applies lee's algorithm to it
     * @param map the 5*5 grid obtained from the bot "look" command
     */
    public Lee(ArrayList<char[]> map){
        // initialize all the distances with the maximum one
        for(int row = 0; row < 5; row++){
            for(int column = 0; column < 5; column++){
                leeMap[row][column] = 26;
            }
        }
        lastVisited = new ArrayList<>();
        Graph graph = new Graph(map);
        graph.linkNodes();
        //get the centre Node and set its distance to 0
        Node startNode = graph.findNode(2, 2);
        if(startNode != null){
            startNode.updateDistanceFromStart(0);
        }
        leeMap[2][2] = 0;
        // apply Lee's algorithm
        graph.lee(graph.findNode(2, 2));
    }

    /**
     * Returns the leeMap generated with Lee's algorithm.
     * @return 5*5 matrix which contains wht shortest distance to every point
                from the centre of it
     */
    public int[][] getLeeMap(){
        return leeMap;
    }
}
