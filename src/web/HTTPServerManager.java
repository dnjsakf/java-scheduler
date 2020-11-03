package web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPServerManager extends Thread {
    
    private final Logger LOGGER = LoggerFactory.getLogger(HTTPServerManager.class);

    private String host = "localhost";
    private int port = 8080;
    
    public HTTPServerManager() {}    
    public HTTPServerManager(int port) {
        this.port = port;
    }
    public HTTPServerManager(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public static void main(String[] args) {
        HTTPServerManager server = new HTTPServerManager();
        server.start();
    }
    
    public void runServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
            
            LOGGER.debug("Server started.");
            LOGGER.debug("Listening for connections on port : " + port + " ...");
            
            // we listen until user halts server execution
            while (true) {
                Socket clientSocket = serverSocket.accept();
                HTTPServerListener client = new HTTPServerListener(clientSocket);
                
                client.start();
            }
            
        } catch (IOException e) {
            LOGGER.error("Server Connection error : " + e.getMessage());
        }
    }
    
    public void run() {
        runServer();
    }
}