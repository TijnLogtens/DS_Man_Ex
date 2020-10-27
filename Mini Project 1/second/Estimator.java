package second;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.DuplicateFormatFlagsException;
import java.util.concurrent.TimeUnit;

import util.QuestionableDatagramSocket;

enum Classes {
    NORMAL,
    DISCARD,
    REORDER,
    DUPLICATE
}

public class Estimator {

    private static int serverPort = 7007;
    private static int clientPort = 8008;
    private static int sentMessages = 0;
    private static int byteSize = 0;
    private static int numberOfDatagramsToSend;
    private static int discardCounter = 0;
    private static int reorderCounter = 0;
    private static int dupblicateCounter = 0;
    private static int normalCounter = 0;

    private static DatagramPacket[] datagramPackets = null;
    private static QuestionableDatagramSocket clientSocket = null;
    private static DatagramSocket serverSocket = null;

    public static void main(String[] args) {
        try {
            if (args.length != 4) throw new Exception("Wrong number of Arguments");
            byteSize = Integer.parseInt(args[0]);
            numberOfDatagramsToSend = Integer.parseInt(args[1]);
            int intervalBetweenTransmission = Integer.parseInt(args[2]);
            int maxRequests = Integer.parseInt(args[3]);
            InetAddress aHost = InetAddress.getByName("localhost");

            clientSocket = new QuestionableDatagramSocket(clientPort);
            serverSocket = new DatagramSocket(serverPort);

            datagramPackets = new DatagramPacket[numberOfDatagramsToSend];
            for (int i = 0; i < datagramPackets.length; i++) {
                String message = i+"";
                datagramPackets[i] = new DatagramPacket(message.getBytes(),
                        message.getBytes().length, aHost, serverPort);
            }
            int requestCounter = 0;
            while (requestCounter < maxRequests) {
                sender();
                receiver();
                TimeUnit.MILLISECONDS.sleep(intervalBetweenTransmission);
                requestCounter++;
            }
            System.out.println("Statistics");
            System.out.println("Discarded = " + discardCounter);
            System.out.println("Duplicated = " + dupblicateCounter);
            System.out.println("Reordered = " + reorderCounter);
            System.out.println("Normal = " + normalCounter);
            System.out.println("Number of Requests = " + requestCounter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null) clientSocket.close();
        }


    }

    private static void sender() throws Exception {
        sentMessages = clientSocket.send(datagramPackets);
    }

    private static void receiver() throws IOException {
        byte[] reciveBytes = new byte[byteSize];
        String[] receivedMessages = new String[sentMessages];
        for (int i = 0; i < sentMessages; i++) {
            DatagramPacket d = new DatagramPacket(reciveBytes, reciveBytes.length);
            serverSocket.receive(d);
            receivedMessages[i] = new String(reciveBytes, StandardCharsets.UTF_8);
            reciveBytes = new byte[byteSize];
        }
        classifier(receivedMessages);
    }


    private static void classifier(String[] receivedMessages) {
        boolean normal = true;
        for (int i = 0; i < datagramPackets.length; i++) {
            int count = 0;
            for(int j=0; j<receivedMessages.length; j++){
                int m = Integer.parseInt(receivedMessages[j].trim());
                if(m==i){
                    count++;
                    if(j!=i){
                        reorderCounter++;
                        normal = false;
                    }
                }
            }
            if(count == 0){
                discardCounter++;
                normal = false;
            } 
            if(count > 1){
                dupblicateCounter++;
                normal = false;
            } 
        }
        if(normal) normalCounter++;
    }
}
