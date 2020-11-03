package websockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;

import websockets.endpoints.ChatEndPoint;

public class SocketServerManager extends Thread {
	
	private final String HOST = "localhost";
	private final int PORT = 3000;
	private final String ROOT_PATH = "/";

	private Server server = null;
	
	public SocketServerManager() {
        this.server = new Server(HOST, PORT, ROOT_PATH, ChatEndPoint.class);
	}

    public void runServer() {
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
    
    public void run() {
    	runServer();
    }
	
    public static void main(String[] args) {
    	SocketServerManager socket_m = new SocketServerManager();
    	
    	socket_m.runServer();
    }
}
