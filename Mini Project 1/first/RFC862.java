package first;

import java.net.*;

public class RFC862 {

    public static void main(String[] args) throws Exception{

        String messageString;
        DatagramSocket serverSocket = new DatagramSocket(7007);
        byte[] reciveBytes = new byte[1000];
        byte[] sendBytes = new byte[1000];
        while(true){
            DatagramPacket serverPacket = new DatagramPacket(reciveBytes, reciveBytes.length);
            serverSocket.receive(serverPacket);
            InetAddress ipAddress = serverPacket.getAddress();
            messageString = new String(serverPacket.getData());
            System.out.println("From client: " + messageString);
            sendBytes = messageString.getBytes();
            DatagramPacket echoPacket = new DatagramPacket(sendBytes, sendBytes.length, ipAddress, serverPacket.getPort());
            serverSocket.send(echoPacket);
            reciveBytes = new byte[1000];
            sendBytes = new byte[1000];
        }

    }
}