import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class which represents the connection between a client and the server.
 * Handles the message flow and extends Thread so the server can be
 * multi-threaded.
 */
public class ConnectionThread extends Thread {

    // declaring fields
    private Socket connection; // client side
    private BufferedReader clientIn;
    private PrintWriter clientOut;
    private final ChatServer server; // server side
    private boolean serverClosed = false;
    private boolean isUsernameSet = false;
    private String userName;
    private static final String CONFIRMATION_MESSAGE = "Thank you for joining the server...enjoy!";

    /**
     * Constructor which creates the data streams between the Client and the server
     * @param connection Client side connection
     * @param server Server side connection
     */
    public ConnectionThread(Socket connection, ChatServer server){
        this.server = server;
        this.connection = connection;
        try {
            InputStreamReader r = new InputStreamReader(connection.getInputStream());
            // Data input stream from Client to server
            clientIn = new BufferedReader(r);
            // Data output stream from server to Client
            clientOut = new PrintWriter(connection.getOutputStream(), true);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Set that the server is closed.
     */
    public void setServerClosed(){
        serverClosed = true;
    }

    /**
     * Return the client side connection
     * @return Socket object representing the client side of the connection
     */
    public Socket getConnection(){
        return connection;
    }

    /**
     * Return the output stream from the server to the client
     * @return PrintWriter object representing the output stream from the server to the Client
     */
    public PrintWriter getClientOut(){
        return clientOut;
    }

    /**
     * Run method of the class. Handles the data flow between a client and the server.
     */
    @Override
    public void run(){
        // set the username of the client
        try {
            while (!isUsernameSet) {
                String username = clientIn.readLine();
                if (server.getUserNames().size() != 0) {
                    if (!server.getUserNames().contains(username)) {
                        isUsernameSet = true;
                        server.addUser(username);
                        userName = username;
                        clientOut.println(CONFIRMATION_MESSAGE);
                        break;
                    }
                    clientOut.println("Please try again...this username is already taken");
                } else {
                    isUsernameSet = true;
                    userName = username;
                    server.addUser(username);
                    clientOut.println(CONFIRMATION_MESSAGE);
                }
            }
        } catch (IOException e) {
            System.out.println("Server has shut down...");
        }
        // listen for messages from the client and make the server broadcast them
        while (!serverClosed){
            try{
                String userInput = clientIn.readLine();
                // check if the user disconnects
                if(userInput.equals("/disconnect")){
                    connection.close();
                    break;
                }
                server.printMessageToAllClients(userInput);
            } catch (IOException e){
                // print to the server console that the user has disconnected
                System.out.println("There is something wrong with " + userName +
                        ". Probably the server is shutting down.");
            } catch (NullPointerException e){
                server.disconnect(userName, this);
                serverClosed = true;
            }
        }
    }
}
