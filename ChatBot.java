import java.io.IOException;

/**
 * Class which represents the chat bot. Extends ChatClient.
 */
public class ChatBot extends ChatClient {

    /**
     * Constructor which calls the super method on its parent.
     * @param address String IP address of the server
     * @param port integer representing the port number of the port
     */
    public ChatBot(String address, int port){
        super(address, port);
    }

    /**
     * Overwrite the setUserName method so the bot's user-name is Quacky
     */
    @Override
    public void setUserName(){
        this.name = "Quacky";
    }

    /**
     * Method from the parent class which is overwritten so that if the bot's user
     * enters input, then the message will not be broadcast. However, the bot should
     * still be able to disconnect from the server.
     */
    @Override
    public void createListeningThread(){
        listeningThread = new Thread(){
            @Override
            public void run(){
                try{
                    String userInput = "";
                    while(!userInput.equals("/disconnect")){
                        if(isServerClosed){
                            break;
                        }
                        userInput = userIn.readLine();
                    }
                    server.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Method from the parent class which is overwritten so that if one client
     * types "hey quacky" in the chat, then the bot will send a message to all the connected
     * clients.
     */
    @Override
    public void createPrintingThread(){
        printingThread = new Thread() {
            @Override
            public void run(){
                try{
                    while(!isServerClosed){
                        String serverResponse = serverIn.readLine();
                        // find the sender's name
                        String senderName = serverResponse.substring(0, serverResponse.indexOf(':'));
                        if(!name.equals(senderName)) {
                            System.out.println(serverResponse);
                        }
                        // check if message is "hey quacky"
                        if(serverResponse.substring(serverResponse.indexOf(":") + 2).
                                toLowerCase().equals("hey quacky")){
                            serverOut.println(name + ": Quack Quack!!");
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
     * Main method which creates a new ChatBot and initializes the object. A connection is made
     * to the client.
     * @param args String[] which contains command line arguments
     *            such as the IP address and the port for the server
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
        ChatBot Quacky = new ChatBot(address, Integer.parseInt(port));
        Quacky.go();
    }

}
