/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootstrapserver.message;

import java.util.ArrayList;
import com.bootstrapserver.model.*;

/**
 *
 * @author Mevan
 */
public class RequestStatusMessage extends Message{
    
    private ArrayList<Peer> activePeers = new ArrayList<Peer>();
    private int accountType;
    private int userID;
    private String status;

    public RequestStatusMessage(){
        super();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Peer> getActivePeers() {
        return activePeers;
    }

    public void setActivePeers(ArrayList<Peer> activePeers) {
        this.activePeers = activePeers;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setTitle(String title){
        this.title = title;
    }

    @Override
    public String messageToString() {
        String msg = super.messageToString();
        msg+=String.valueOf(userID);
        msg+=status;
        if (status.equals("Success")) {
            if (!(activePeers.isEmpty())) {
                for (Peer p : activePeers) {
                    msg += ("," + p.peerToString());
                }
            }
        }
        msg+="\n";
        return msg;
    }

}
