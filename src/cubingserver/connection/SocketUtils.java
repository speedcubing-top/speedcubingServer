package cubingserver.connection;

import org.bukkit.Bukkit;

import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtils {
    public static ServerSocket socket;

    public static void UnHandledSendData(int port, String data, int timeout) throws Exception {
        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", port), timeout);
        new DataOutputStream(clientSocket.getOutputStream()).writeBytes(data);
        clientSocket.close();
    }

    public static void sendData(int port, String data, int timeout) {
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress("localhost", port), timeout);
            new DataOutputStream(clientSocket.getOutputStream()).writeBytes(data);
            clientSocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void Load(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}