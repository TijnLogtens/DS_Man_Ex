package util;

import java.io.Serializable;

public class Message implements Serializable {

    public Message(MessageType messageType, int id, String message) {
        this.messageType = messageType;
        this.id = id;
        this.message = message;
    }

    public MessageType messageType;
    public int id;
    public String message;
}
