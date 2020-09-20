package third;

import java.io.IOException;
import java.net.*;

public class ReliableUDPClient {
    private static int serverPort;
    private static int clientPort = 8008;
    private static String ipAdress;
    private static String message;
    private static DatagramSocket aSocket = null;

    private static void send() throws IOException {
        byte[] msgBytes = message.getBytes();
        InetAddress aHost = InetAddress.getByName(ipAdress);
        DatagramPacket request = new DatagramPacket(msgBytes, msgBytes.length, aHost, serverPort);
        aSocket.send(request);
    }

    private static void sendConfirm() throws IOException {
        byte[] msgBytes = "confirm".getBytes();
        InetAddress aHost = InetAddress.getByName(ipAdress);
        DatagramPacket request = new DatagramPacket(msgBytes, msgBytes.length, aHost, serverPort);
        aSocket.send(request);
    }

    private static void receive() throws IOException {
        byte[] buffer = new byte[255]; // Allocate a buffer into which the reply message is written
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        try{
            aSocket.setSoTimeout(5000);
            aSocket.receive(reply);
            String receivedMessage = new String(reply.getData()).trim();
            if (receivedMessage.equals(message)){
                sendConfirm();
            } else{
                send();
                receive();
            }
        }catch (SocketTimeoutException ex) {
            send();
            receive();
        }
    }



    public static void main(String args[]) throws Exception {
        if (args.length < 3) throw new Exception("Wrong number of Arguments");
        serverPort = Integer.parseInt(args[0]);
        ipAdress = args[1];
        message = args[2];
        if(message.length() >= 255) throw new Exception("Too long message");
        try {
            aSocket = new DatagramSocket(8008);
            send();
            receive();
        } catch (SocketException e) { // Handle socket errors
            System.out.println("Socket exception: " + e.getMessage());
        } catch (IOException e) { // Handle IO errors
            System.out.println("IO exception: " + e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        } finally { // Close socket
            if (aSocket != null)
                aSocket.close();
        }

    }
}
