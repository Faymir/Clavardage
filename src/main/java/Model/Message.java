package Model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String from = null;
    private Date date = null;
    private String message = "";
    private String to = null;
    private boolean server_msg = false;

    public Message() {
    }

    public Message(String from, Date date, String message, String to) {
        this.from = from;
        this.date = date;
        this.message = message;
        this.to = to;
    }

    public Message(String from, Date date, String message, String to, boolean server_msg){
        this(from, date, message, to);
        this.server_msg = server_msg;
    }

    public boolean isReceivedMessage(){
        return (this.to == null && !isServerMsg());
    }

    public boolean isSendedMessage(){
        return this.from == null && !isServerMsg();
    }

    public boolean isServerMsg() {
        return server_msg;
    }

    public void setServerMsg(boolean server_msg) {
        this.server_msg = server_msg;
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
