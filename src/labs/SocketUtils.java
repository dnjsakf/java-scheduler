package labs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


class GameProtocol {

    public String processInput(String theInput) {

        String theOutput = null;

        theOutput = theInput;

        return theOutput;
    }
}

class ServerThread extends Thread{

    private Socket socket = null;
    private GameProtocol gp;

    public ServerThread(Socket socket, GameProtocol gp) {
        super("ServerThread");
        this.socket = socket;
        this.gp = gp;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                ) {
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                outputLine = gp.processInput(inputLine);
                System.out.println(outputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class SocketUtils {
    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            GameProtocol gp = new GameProtocol();

            ServerThread player= new ServerThread(serverSocket.accept(), gp);
            player.start();

        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
            System.exit(-1);
        }

    }
}
