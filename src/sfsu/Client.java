package sfsu;

import javax.print.DocFlavor;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Verifies that the database server is able to receive a client request. Prints the response from the server.
 *
 * Invocation and arguments:
 *   java -cp lib/*:out/src/sfsu sfsu.Client <IP> <port>
 */
public class Client  implements Runnable {
    //This variable holds the keys that have been used to store values in database
    //helps with looking up values
    ArrayList<String> keys;
    //These variables are used to control the multi-thread part of the program
    private static final int TOTAL_WORKERS = 100;
    private static final int CONCURRENT_THREADS = 25;

    //removes keys of deleted values in database from the list of keys we can use to look up values
    public synchronized void deleteKey(String key) {
        this.keys.remove(this.keys.indexOf(key));
    }

    //gets the key of the keys list at position i
    public String getKey(int i) {
        return this.keys.get(i);
    }

    //adds a key to the keys list
    public synchronized void add(String val) {
        keys.add(val);
    }

    //initializes the list of keys
    public Client() {
        keys = new ArrayList<>();
    }

    public void run() {
        //gives the address of the server
        String serverAddress = "localhost";
        //gives the port number the server is using
        int port = 3000;

        Random rand = new Random();
        pick:
        {
            //creates a random int to help the threads what operation to use
            int r = rand.nextInt(3);


            String key;
            String val;
            //case r == 0. Grabs a key at random from key list and sends a GET request to server
            if (r == 0) {
                if (keys.size() > 0) {
                    key = this.getKey(rand.nextInt(this.keys.size()));
                    DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
                            .setOperation(DatabaseProtos.Request.OperationType.GET)
                            .setKey(key)
                            .build();
                    try {
                        // Create a socket and attempt to connect.
                        Socket clientSocket = new Socket(serverAddress, port);

                        // Write the request message to the socket.
                        request.writeDelimitedTo(clientSocket.getOutputStream());
                        System.out.println("Request sent, waiting for response.");

                        // Receive and parse a response from the server.
                        DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
                        System.out.println(String.format("Response received: %s\n", response));

                        // Close the sockets and finish.
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    break pick;
                }
            //In case r == 1. Generates a random key and grabs a random value from Random_Key_Value.java and sends
            //a PUT request to server to store value based on key value
            } else if (r == 1) {
                key = Random_Key_Value.getRandKey();
                this.add(key);
                val = Random_Key_Value.getRandValue();
                DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
                        .setOperation(DatabaseProtos.Request.OperationType.PUT)
                        .setKey(key)
                        .setValue(val)
                        .build();
                try {
                    // Create a socket and attempt to connect.
                    Socket clientSocket = new Socket(serverAddress, port);

                    // Write the request message to the socket.
                    request.writeDelimitedTo(clientSocket.getOutputStream());
                    System.out.println("Request sent, waiting for response.");

                    // Receive and parse a response from the server.
                    DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
                    System.out.println(String.format("Response received: %s\n", response));

                    // Close the sockets and finish.
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //case default. Grabs a random key value from keys list and sends  a DELETE request to server
            //to delete the value from the database given the key.
            } else {
                if (keys.size() > 0) {
                    key = this.getKey(rand.nextInt(this.keys.size()));
                    this.deleteKey(key);
                    DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
                            .setOperation(DatabaseProtos.Request.OperationType.DELETE)
                            .setKey(key)
                            .build();
                    try {
                        // Create a socket and attempt to connect.
                        Socket clientSocket = new Socket(serverAddress, port);

                        // Write the request message to the socket.
                        request.writeDelimitedTo(clientSocket.getOutputStream());
                        System.out.println("Request sent, waiting for response.");

                        // Receive and parse a response from the server.
                        DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
                        System.out.println(String.format("Response received: %s\n", response));

                        // Close the sockets and finish.
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    break pick;
                }
            }
        }
    }

    /**
     * Connects to the sever process. Sends a dummy request and receives a response.
     * @throws Exception whenever anything bad happens. This avoids the need to wrap most instructions in try/catch
     *      blocks. Good enough for a dummy tester.
     */
    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        Client futuresExample = new Client();

        for (int times = 0; times < TOTAL_WORKERS; times++) {
            executorService.submit(futuresExample);
        }

        executorService.shutdown();

    }

}
