package third;

import java.net.*;

public class ReliableUDPServer {

    public static void main(String[] args) throws Exception{

        String messageString;
        DatagramSocket serverSocket = new DatagramSocket(7007);
        byte[] reciveBytes = new byte[255];
        byte[] sendBytes;
        while(true){
            DatagramPacket serverPacket = new DatagramPacket(reciveBytes, reciveBytes.length);
            serverSocket.receive(serverPacket);
            InetAddress ipAddress = serverPacket.getAddress();
            messageString = new String(serverPacket.getData());
            sendBytes = messageString.getBytes();
            DatagramPacket echoPacket = new DatagramPacket(sendBytes, sendBytes.length, ipAddress, serverPacket.getPort());
            serverSocket.send(echoPacket);
            reciveBytes = new byte[255];
            serverPacket = new DatagramPacket(reciveBytes, reciveBytes.length);
            serverSocket.receive(serverPacket);
            String confirmString = new String(serverPacket.getData()).trim();
            if(confirmString.equals("confirm")){
                System.out.println(messageString);
            }
            reciveBytes = new byte[255];
        }

    }
}