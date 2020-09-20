package second;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import util.QuestionableDatagramSocket;
import util.WorkingDatagramSocket;

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
            String[] messages = {"Hello", "Goodbye"};
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
                String message = (i + "_" + messages[i % 2]);
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
        switch (classifier(receivedMessages)) {
            case DISCARD -> discardCounter++;
            case REORDER -> reorderCounter++;
            case DUPLICATE -> dupblicateCounter++;
            case NORMAL -> normalCounter++;
        }
    }


    private static Classes classifier(String[] receivedMessages) {
        if (sentMessages < numberOfDatagramsToSend) {
            return Classes.DISCARD;
        } else if (sentMessages > numberOfDatagramsToSend) {
            return Classes.DUPLICATE;
        }
        if (sentMessages == numberOfDatagramsToSend) {
            for (int i = 0; i < receivedMessages.length; i++) {
                if (Integer.parseInt(receivedMessages[i].split("_")[0]) != i) {
                    return Classes.REORDER;
                }
            }
        }

        return Classes.NORMAL;
    }
}
