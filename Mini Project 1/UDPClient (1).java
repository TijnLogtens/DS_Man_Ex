import java.net.*;
import java.io.*;
import java.util.Scanner;

public class UDPClient {
    private static int serverPort = 7007;

    public static void main(String args[]) {
        QuestionableDatagramSocket aSocket = null;
        try {
            aSocket = new QuestionableDatagramSocket(8008);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Scanner msgScan = new Scanner(System.in);
        try {
            // Read a message from standard input
            String msg = "Hello";
            String msg2 = "Goodbye";
            byte[] msgBytes = msg.getBytes();
            byte[] msgBytes2 = msg2.getBytes();

            // Send the message

            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(msgBytes, msgBytes.length, aHost, serverPort);
            DatagramPacket request2 = new DatagramPacket(msgBytes2, msgBytes2.length, aHost, serverPort);
            DatagramPacket[] requests = {request, request2};
            aSocket.send(requests);

            System.out.println(aHost);

            //Receive reply
            while (true){
                byte[] buffer = new byte[1000]; // Allocate a buffer into which the reply message is written
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);

                // Print reply message
                System.out.println("Reply: " + new String(reply.getData()));
            }


        } catch (
                SocketException e) { // Handle socket errors
            System.out.println("Socket exception: " + e.getMessage());
        } catch (
                IOException e) { // Handle IO errors
            System.out.println("IO exception: " + e.getMessage());
        } finally { // Close socket
            if (aSocket != null)
                aSocket.close();
        }
    }
}