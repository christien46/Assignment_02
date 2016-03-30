

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;



public class File_Server {

    private ServerSocket serverSocket = null;

    /*
     * The server socket is initiated
     */

    public File_Server(int port){
        try{
            serverSocket = new ServerSocket(port);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /*
     *A new thread is created for each client socket that connects to the server
     *An instance of ClientConnectionHandler is created for each client socket that connects
     *C.start is called to implement the runnable function in the ClientConnectionHandler class
     */
    public void StartUp(){
        System.out.println("Waiting for users");

        try{

            while(true) {

                Socket clientSocket = serverSocket.accept();
                ClientConnectionHandler Client = new ClientConnectionHandler(clientSocket);
                System.out.println("A new Client has joined!");
                Thread C = new Thread(Client);
                C.start();

            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args){

        File_Server file_Server = new File_Server(8080);
        file_Server.StartUp();

    }

}
