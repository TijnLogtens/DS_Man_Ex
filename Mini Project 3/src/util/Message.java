package util;

import java.io.Serializable;

public class Message implements Serializable {

    public Message(MessageType messageType, int id, String message) {
        this.messageType = messageType;
        this.id = id;
        this.message = message;
        this.direction = 0;
    }

    public MessageType messageType;
    public int id;
    public String message;
    public int direction;

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", id=" + id +
                ", message='" + message + '\'' +
                ", direction=" + direction +
                '}';
    }
}
