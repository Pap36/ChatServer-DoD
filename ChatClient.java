import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Class representing the client. Handles receiving input from the user, sending it to the server
 * and printing messages from the server to the user.
 */
public class ChatClient {

    // declaring fields
    public Socket server;
    public String name; // client's username
    public BufferedReader userIn; // BufferedReader for user input
    public PrintWriter serverOut; // Writer to the server
    public BufferedReader serverIn; // BufferedReader for the server's messages
    public boolean isServerClosed = false; // value is true if the server is closed
    public Thread printingThread;
    public Thread listeningThread;
    private boolean isPlaying = false; // value is true if user is playing DOD
    // String containing the instructions for the user when joining the server
    private static final String CONFIRMATION_MESSAGE = "Thank you for joining the server...enjoy!\n" +
            "To play DOD, enter in the" + " chat the command: '/play DOD'.\n" + "To make Quacky quack, just say" +
            " in the chat: 'hey Quacky'.\n" + "If Quacky does not quack, it might be that it isn't connected.";

    /**
     * Constructor which establishes the connection to the server and creates the input and output streams
     * @param address String representing the IP address of the server
     * @param port integer representing the port of the server
     */
    public ChatClient(String address, int port){
        try{
            server = new Socket(address, port);
            userIn = new BufferedReader(new InputStreamReader(System.in));
            serverOut = new PrintWriter(server.getOutputStream(), true);
            serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch(ConnectException e){
            System.out.println("Whoops...Looks like the server is down...");
            System.exit(0);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Set the user-name of the client. Make sure client inputs a valid user-name.
     * Because of the implementation, client cannot input an empty username, a username containing ":", or the
     * username Quacky, since that is the username of the Bot. The client cannot have the user-name "Server".
     */
    public void setUserName(){
        try {
            do{
                System.out.println("Your username must contain at least 1 character and it cannot be ':'.");
                System.out.println("Please enter the username you wish to use in the chat: ");
                name = userIn.readLine();
            }while (name.equals("") || name.contains(":") || name.equals("Quacky") || name.equals("Server"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the instance of the class.
     * @return ChatClient object representing the user.
     */
    public ChatClient getInstance(){
        return this;
    }

    /**
     * Returns the BufferedReader used to get input from the user.
     * @return BufferedReader object used to get input from the user.
     */
    public BufferedReader getUserIn(){
        return userIn;
    }

    /**
     * Creates a new inner thread object which listens for input from the user.
     * Handles 2 special commands, "/disconnect" and "/play DOD".
     */
    public void createListeningThread(){
        listeningThread = new Thread() {
            @Override
            public void run(){
                try{
                    String userInput = userIn.readLine();
                    // if the user inputs "/disconnect", then close the connection
                    while(!userInput.equals("/disconnect")){
                        // if the user inputs "/play DOD", then start a new game for the user
                        if(userInput.equals("/play DOD")){
                            // update the value of "isPlaying" accordingly
                            isPlaying = true;
                            new DungeonsOfDoom(getInstance());
                            isPlaying = false;
                        }
                        else{
                            /*
                            Send the user's message to the server.
                            Use the format: "user-name: message".
                             */
                            serverOut.println(name + ": " + userInput);
                        }
                        if(isServerClosed){
                            break;
                        }
                        userInput = userIn.readLine();
                    }
                    // close the connection with the server
                    server.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Creates a new inner thread object which listens for input from the server.
     * If received, it then prints the message to the user, as long as the user is not the
     * sender, or the user is not playing.
     */
    public void createPrintingThread(){
        printingThread = new Thread() {
            @Override
            public void run(){
                try{
                    while(!isServerClosed){
                        String serverResponse = serverIn.readLine();
                        // find the sender's username
                        String senderName = serverResponse.substring(0, serverResponse.indexOf(':'));
                        if(!name.equals(senderName) && !isPlaying) {
                            System.out.println(serverResponse);
                        }
                    }
                } catch (IOException e){
                    System.out.println("Disconnecting from the server...");
                    System.out.println("You disconnected from the server...hope to see you back soon!");
                } catch (NullPointerException e){
                    System.out.println("Whoops...Looks like the server is down...");
                    isServerClosed = true;
                    System.exit(0);
                }
            }
        };
    }

    /**
     * Method which makes sure the chosen user-name is available and then,
     * it starts the threads which handle the message flow between the server and the client.
     */
    public void go(){
        setUserName();

        // send the chosen user-name to the server and wait for a confirmation that it is available
        try{
            serverOut.println(name);
            String confirmation = serverIn.readLine();
            // confirmation from the server
            while(!confirmation.equals("Thank you for joining the server...enjoy!")){
                // keep trying until a valid user-name is entered
                System.out.println(confirmation);
                setUserName();
                serverOut.println(name);
                confirmation = serverIn.readLine();
            }
            System.out.println(CONFIRMATION_MESSAGE);
        } catch (IOException e){
            e.printStackTrace();
        } catch (NullPointerException e){
            System.out.println("Whoops...Looks like the server is down...");
            System.exit(0);
        }

        createListeningThread();
        createPrintingThread();

        listeningThread.start();
        printingThread.start();
    }

    /**
     * Main method which establishes the connection to the server.
     * @param args String[] containing the command line arguments
     *             which can specify the IP address and port of the server
     */
    public static void main(String[] args) {
        // default address and port
        String address = "localhost";
        String port = "14001";
        for(int index = 0; index < args.length; index++){
            if(args[index].equals("-cca")){
                address = args[index+1];
                index++;
            }
            else if (args[index].equals("-ccp")){
                port = args[index+1];
            }
        }
        ChatClient myClient = new ChatClient(address, Integer.parseInt(port));
        myClient.go();
    }
}
