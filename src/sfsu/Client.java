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
    ArrayList<String> keys;
    private static final int TOTAL_WORKERS = 40;
    private static final int CONCURRENT_THREADS = 20;

    public synchronized void deleteKey(String key) {
        this.keys.remove(this.keys.indexOf(key));
    }

    public String getKey(int i) {
        return this.keys.get(i);
    }

    public synchronized void add(String val) {
        keys.add(val);
    }

    public Client() {
        keys = new ArrayList<>();
    }

    public void run() {
        String serverAddress = "localhost";
        int port = 3000;
        Random rand = new Random();
        int r = rand.nextInt(3);
        String key;
        String val;
        if (r == 0) {
            if(keys.size() > 0) {
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
            }
        } else if(r == 1) {
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
        } else {
            if(keys.size() > 0) {
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
            }
        }
        System.out.println(this.keys.size());
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

        return;
    }

}
