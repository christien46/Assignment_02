import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.*;
import java.net.*;



public class Client_Server extends Application{




    //Sockets
    private String hostname = null;
    private int port = 0;
    private static Socket Sock;
    public String Selected;

    //Lists
    private ListView<String> listViewA;
    private ListView<String> listViewB;


    //Stream
    private static PrintStream Pstream;
    /*
    * The start function sets up the T chart which contains the list of the files in each folder as well as showing the buttons
    * Using Listviews, the contents of each folder was displayed. The buttons when clicked will execute functions defined below.
    * Clicking the Download button and highlighting a specific text file on the right will execute the download command while
    * highlighting a file on the left and clicking the upload button will execute the Upload function
    */
    @Override
    public void start(Stage window) throws Exception{

        BorderPane layout = new BorderPane();

        //Scene
        Scene scene = new Scene(layout, 800, 800);

        //GridPane
        GridPane editArea = new GridPane();
        editArea.setPadding(new Insets(0, 0, 0, 0));
        editArea.setVgap(0);
        editArea.setHgap(0);

        //SplitPane
        SplitPane sPane = new SplitPane();
        sPane.setPrefWidth(scene.getWidth());
        sPane.setPrefHeight(scene.getHeight());


        //Buttons
        Button Download = new Button("Download");
        Button Upload = new Button("Upload");

        //Folders
        File UF = new File("UploadFolder");
        File SF = new File("ServerFolder");

        //Display Text files in folders
        listViewA = new ListView<>();
        listViewA.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listViewA.getItems().addAll(UF.list());

        listViewB = new ListView<>();
        listViewB.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listViewB.getItems().addAll(SF.list());

        sPane.getItems().addAll(listViewA,listViewB);
        sPane.setDividerPosition(5,0.5);


        Pstream = new PrintStream(Sock.getOutputStream());


        Download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Selected = listViewB.getSelectionModel().getSelectedItem();
                String file = "ServerFolder/" + Selected;
                Pstream.println("Download");
                Pstream.println(file);
                Download(file);

                try {
                    window.close();
                    start(window);
                    Sock.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        Upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Selected = listViewA.getSelectionModel().getSelectedItem();
                String file = "UploadFolder/" + Selected;
                System.out.println(file);
                Pstream.println("Upload");
                Pstream.println(file);
                Upload(file);
                try {
                    window.close();
                    start(window);
                    Sock.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("The file " + Selected  + " was sent to the folder ServerFolder");

            }
        });

        editArea.add(Download,0,0);
        editArea.add(Upload,1,0);
        layout.setTop(editArea);

        layout.setCenter(sPane);

        window.setTitle("File Sharer v1.0");
        window.setScene(scene);
        window.show();

    }

    /*
    * gets the Ip address as well as the port used to start up the Client
    */
    public void startUP(String ip, int port) {
        this.hostname = ip;
        this.port = port;

    }
    /*
    * initiates the client Socket
    */
    public void connect() {
        try {
            Sock = new Socket(hostname, port);
            System.out.println("Socket connected");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * The Upload function takes the the path of a file being uploaded and converts it to bytes placed in an array
    * A new BufferedInputStream is created to read the bytes of the File, sFile
    * DataInputStream is created to read data fromm the buffStream
    * buffSteam reads the array of bytes
    * An OutputStream is created to get bytes from Sock
    * dataOutStream writes data to the oStream( writes name, information and then clears)
    */
    public void Upload(String StringFile){
        try {

            File sFile = new File(StringFile);
            byte[] bytes = new byte[(int) sFile.length()];
            BufferedInputStream buffStream = new BufferedInputStream(new FileInputStream(sFile));
            buffStream.read(bytes,0,bytes.length);         //Reads some number of bytes from the input stream and stores them into the buffer array b.

            DataInputStream dataInStream = new DataInputStream(buffStream);
            dataInStream.read(bytes);

            OutputStream oStream = Sock.getOutputStream();

            DataOutputStream dataOutStream = new DataOutputStream(oStream);
            dataOutStream.writeUTF(sFile.getName());
            dataOutStream.writeLong(bytes.length);
            dataOutStream.write(bytes, 0, bytes.length);
            dataOutStream.flush();

        }catch(IOException e){
            e.printStackTrace();
        }


    }


    /*
    * InputStream is set to Socket Sock
    * DataInputStream is set to read from the Istream
    * Selected File and its string of characters is decoded from the UTF and returned as String
    * create a new array of bytes of size 1024
    * while information is still there Ostream is to write what the buffer has received
    * */
    public void Download(String SelectedFile){
        try{
            InputStream Istream = Sock.getInputStream();
            DataInputStream Dstream = new DataInputStream(Istream);

            SelectedFile = Dstream.readUTF();
            OutputStream Ostream = new FileOutputStream( "UploadFolder/"+ SelectedFile);
            //long size = Dstream.readLong();
            byte[] buffer = new byte[1024];
            int holder = -1;
            while((holder = Dstream.read(buffer)) > 0){
                Ostream.write(buffer);
            }
            Ostream.close();
            Istream.close();

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    /*
    * ClientServer is created and initiated using the following address and port number
    * the Interface for the system is then launched
    * */
    public static void main(String[] args)throws IOException{

        Client_Server Client = new Client_Server();
        Client.startUP("127.0.0.1", 8080);
        Client.connect();
        Pstream = new PrintStream(Sock.getOutputStream());
        launch(args);

    }


}
