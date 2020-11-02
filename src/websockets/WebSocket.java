package websockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;

import websockets.endpoints.ChatEndPoint;

public class WebSocket {
    public static void main(String[] args) {
        runServer();
    }

    public static void runServer() {
        
        Server server = new Server("localhost", 3000, "/", ChatEndPoint.class);

        try {

            server.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Please press a key to stop the server.");

            reader.readLine();

        } catch (Exception e) {

            throw new RuntimeException(e);

        } finally {

            server.stop();

        }

    }
}
