package sfsu;

import java.net.Socket;

/**
 * Verifies that the database server is able to receive a client request. Prints the response from the server.
 *
 * Invocation and arguments:
 *   java -cp lib/*:out/src/sfsu sfsu.Client <IP> <port>
 */
public class Client {

    /**
     * Connects to the sever process. Sends a dummy request and receives a response.
     * @throws Exception whenever anything bad happens. This avoids the need to wrap most instructions in try/catch
     *      blocks. Good enough for a dummy tester.
     */
    public static void main(String[] args) throws Exception {
        // Parse the IP and port
        String serverAddress = "localhost";
        int port = 3000;

        // Create a dummy request. None of the arguments are important, as long as the request is syntactically valid.
        DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
                .setOperation(DatabaseProtos.Request.OperationType.PUT)
                .setKey("An example request key")
                .setValue("An example request value")
                .build();

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
        return;
    }

}
