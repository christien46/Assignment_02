import java.io.*;
import java.net.Socket;
public class ClientConnectionHandler implements Runnable {

    public Socket Sock;


    //Streams
    private BufferedReader buffReader;




    public ClientConnectionHandler(Socket S){

        this.Sock = S;
    }
    /*
    * This function takes the messages read out from the Socket outputStream and determines what the lient wishe to do
    * If the buffReader finds the word "Upload"  then it will run the receivedUploadFile function
    * If the buffReader finds the word "download" then it will run the SendingFileToClient function
    *
    */
    public void run() {
        try {
            buffReader = new BufferedReader(new InputStreamReader(Sock.getInputStream()));
            String line = null;
            while ((line = buffReader.readLine()) != null) {
                switch (line) {

                    case "Upload":
                        receivedUploadFile();
                        break;

                    case "Download":
                        String toClient;
                        while ((toClient = buffReader.readLine()) != null) {
                            sendingFileToClient(toClient);
                        }
                }


                buffReader.close();
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    * This function takes the data that the client socket upload and starts to download the file
    * First the file is converted into bytes and read using a bufferReader
    * The information is saved to the Server folder via the outPutStream
    * */
    public void receivedUploadFile(){
        try {

            DataInputStream clientData = new DataInputStream(Sock.getInputStream());

            String fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(( "ServerFolder/"+ fileName));
            byte[] buffer = new byte[1024];
            int size = -1;
            while ((size = clientData.read(buffer)) > 0) {
                output.write(buffer);
            }
            System.out.println("File "+ fileName + " received from client.");
            output.close();
            clientData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*
     * This function converts the file to an array of bytes which is read using a bufferdInputStream
     * The new File is then sent to the socket using an outputStream
     * dataOutStream writes data to the oStream( writes name, information to the file and then clears)
     */
    public void sendingFileToClient(String file){
        try {

            File thisFile = new File(file);
            byte[] bytes = new byte[(int) thisFile.length()];

            BufferedInputStream Bstream = new BufferedInputStream(new FileInputStream(thisFile));
            Bstream.read(bytes);

            DataInputStream Dstream = new DataInputStream(Bstream);

            OutputStream Ostream = Sock.getOutputStream();

            DataOutputStream DOstream = new DataOutputStream(Ostream);

            DOstream.writeUTF(thisFile.getName());
            DOstream.writeLong(bytes.length);
            DOstream.write(bytes);
            DOstream.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
