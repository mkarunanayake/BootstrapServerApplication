/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import messenger.ServerHandler;

import java.io.Serializable;

/**
 * @author Mevan
 */
public abstract class Message implements Serializable, Cloneable {

    protected String title;
    private int senderID;
    private String receiverAddress;
    private int receiverPort;
    private String senderAddress;
    private int senderPort;
    private long timestamp;
    private int receiverID;

    public Message(String title) {
        this.title = title;
        setSenderPort(ServerHandler.getPort());
        setSenderAddress(ServerHandler.getIpAddress().getHostAddress());
        setSenderID(ServerHandler.getUserID());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }
}
