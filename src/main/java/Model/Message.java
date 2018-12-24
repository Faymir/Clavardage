package Model;

import java.util.Date;

public class Message {
    private String from = null;
    private Date date = null;
    private String message = "";
    private String to = null;

    public Message() {
    }

    public Message(String from, Date date, String message, String to) {
        this.from = from;
        this.date = date;
        this.message = message;
        this.to = to;
    }

    public boolean isReceivedMessage(){
        return this.to == null;
    }

    public boolean isSendedMessage(){
        return this.from == null;
    }

    public String getSender() {
        return from;
    }
    public String getReceiver() {
        return this.to;
    }
    public void setSender(String from) {
        this.from = from;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
