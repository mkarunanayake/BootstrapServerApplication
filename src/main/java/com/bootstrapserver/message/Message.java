/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootstrapserver.message;

import java.io.Serializable;

/**
 *
 * @author Mevan
 */
public abstract class Message implements Serializable{

    protected String title;
    private int senderID;
    private String receiverAddress;
    private int receiverPort;
    private String senderAddress;
    private int senderPort;
    private long timestamp;

    public Message(String title){
        this.title = title;
    }

    public Message(){}

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

    public String messageToString(){
        return title+","+String.valueOf(senderID)+","+ receiverAddress +","+Integer.toString(receiverPort)+","+senderAddress+","+Integer.toString(senderPort)+","+timestamp;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getSenderID() {
        return senderID;
    }
}
