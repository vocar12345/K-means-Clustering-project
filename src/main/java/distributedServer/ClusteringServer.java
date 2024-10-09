package distributedServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClusteringServer {

    private static final int SERVER_PORT = 9001;

    public ClusteringServer() throws IOException {
        @SuppressWarnings("resource")
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server is listening on port " + SERVER_PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            Thread calculateThread = new Thread(new CalculateThread(socket));
            calculateThread.start();
            System.out.println("Obtaining data for new calculation...");
        }
    }

    public static void main(String[] args) {//for sake of simplicity we just put a new main here
        try {
            new ClusteringServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
