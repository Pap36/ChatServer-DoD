import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server class which starts the server and accepts connections from new clients.
 */
public class ChatServer {

    // declaring the fields
    private boolean closeTheServer = false; // value is true if the server should be closed
    private ServerSocket in; // represents the server
    private String userInput;
    private ArrayList<ConnectionThread>connectedClients;
    private ArrayList<String> userNames = new ArrayList<>();

    /**
     * Constructor which opens the server on a specified port and initializes the connectedClients ArrayList.
     * @param port integer representing the port on which the server should be opened.
     */
    public ChatServer(int port){
        connectedClients = new ArrayList<>();
        try{
            in = new ServerSocket(port);
            System.out.println("Listening for connections...");
        } catch (IOException e){
            e.printStackTrace();
        }
        /*
        Create an inner thread which listens if the server's user types into the console the command
        "EXIT", which closes the server.
         */
        Thread disconnect = new Thread(){
            @Override
            public void run(){
                try{
                    BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                    // keep listening for user input until the command is "EXIT"
                    do {
                        userInput = userIn.readLine();
                    } while (!userInput.equals("EXIT"));
                    // close each connection with the clients
                    for(ConnectionThread client:connectedClients){
                        client.setServerClosed();
                        client.getConnection().close();
                    }
                    // close the server
                    in.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        // start the thread
        disconnect.start();
    }

    /**
     * Add a new userName to the list of connected client's user-names.
     * @param userName String representing the username of a new client.
     */
    public void addUser(String userName){
        userNames.add(userName);
    }

    /**
     * Get the list of all connected client's user-names.
     * @return ArrayList of Strings representing the user-names of the connected clients.
     */
    public ArrayList<String> getUserNames(){
        return userNames;
    }

    /**
     * Broadcast a message received by the server from a client, to all clients.
     * @param message String representing the received message to be broadcast.
     */
    synchronized public void printMessageToAllClients(String message){
        for (ConnectionThread client: connectedClients) {
            client.getClientOut().println(message);
        }
    }

    /**
     * Close the connection with a client which has disconnected.
     * @param userName String representing the client's user-name
     * @param connection ConnectionThread representing the connection between the server and the client
     */
    public void disconnect(String userName, ConnectionThread connection){
        System.out.println(userName + " has disconnected...:(");
        // remove the user-name from the list of connected user-names and close the connection
        userNames.remove(userName);
        connectedClients.remove(connection);
    }

    /**
     * Listen for a new connection. If one is established, create a connection between the server and the new client.
     */
    public void go(){
        try{
            Socket newClient = in.accept();
            System.out.println("Connection accepted...");
            // create a new thread representing the connection between the client and the server
            ConnectionThread client = new ConnectionThread(newClient, this);
            connectedClients.add(client);
            client.start();
        } catch(IOException e){
            closeTheServer = true;
            System.out.println("Server is shutting down...");
        }
    }

    /**
     * Main method of the class. Creates a new instance of the class and starts the server.
     * @param args String[] representing the command line arguments
     */
    public static void main(String[] args){
        String port = "14001"; // default port
        // check to see if user has requested the server to open on a different port than the default one
        for(int index = 0; index < args.length; index++){
            if(args[index].equals("-csp")){
                port = args[index+1];
            }
        }
        // create a new object
        ChatServer myServer = new ChatServer(Integer.parseInt(port));
        // keep listening for new connections until the server is shut down
        while(!myServer.closeTheServer){
            myServer.go();
        }
    }
}
