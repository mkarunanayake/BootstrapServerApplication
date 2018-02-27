/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootstrapserver.reciever;

import com.bootstrapserver.model.Peer;
import com.bootstrapserver.model.User;
import com.bootstrapserver.repository.PeerRepository;
import com.bootstrapserver.repository.UserRepository;
import com.bootstrapserver.util.Main;
import com.bootstrapserver.validator.MessageValidator;
import message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Mevan
 */
public class Receiver implements Runnable {

    private final Socket senderSocket;
    private MessageValidator messageValidator;
    private UserRepository userRepo;
    private PeerRepository peerRepo;

    public Receiver(Socket senderSocket) throws SQLException {
        this.senderSocket = senderSocket;
        messageValidator = new MessageValidator();
        userRepo = new UserRepository();
        peerRepo = new PeerRepository();
    }


    @Override
    public void run() {
        try {
            ObjectInputStream is = new ObjectInputStream(senderSocket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(senderSocket.getOutputStream());
            Message msg = (Message) is.readObject();
            if (messageValidator.validate(msg)) {
                String error = "Success";
                RequestStatusMessage requestStatus = new RequestStatusMessage();
                if (msg.getTitle().equals("Login")) {
                    requestStatus.setTitle("LoginStatus");
                    LoginMessage loginMsg = (LoginMessage) msg;
                    User user = userRepo.getUser(loginMsg.getUsername());
                    if (user == null) {
                        error = "Invalid user details!";
                    } else if (!user.getPassword().equals(loginMsg.getPassword())) {
                        error = "Invalid user details!";
                    } else {
                        Main.increaseLoggedInUsers();
                        Peer peer = new Peer(user.getUserID(), loginMsg.getSenderAddress(), loginMsg.getSenderPort());
                        peer.setLastSeen(new Date(System.currentTimeMillis()).getTime());
                        peerRepo.updatePeerInfo(peer);
                        requestStatus.setUserID(user.getUserID());
                    }
                    requestStatus.setStatus(error);
                } else if (msg.getTitle().equals("Register")) {
                    RegisterMessage regMsg = (RegisterMessage) msg;
                    requestStatus.setTitle("RegisterStatus");
                    if (userRepo.getUser(regMsg.getUsername()) != null) {
                        error = "Username Already Taken!";
                        requestStatus.setStatus(error);
                    } else {
                        Main.increaseLoggedInUsers();
                        User user = new User(Main.giveUserID(), regMsg.getUsername(), regMsg.getPassword(), 2);
                        userRepo.saveUser(user);
                        Main.getRegistrationListener().updateUI(user);
                        Peer peer = new Peer(user.getUserID(), regMsg.getSenderAddress(), regMsg.getSenderPort());
                        peer.setLastSeen(new Date(System.currentTimeMillis()).getTime());
                        peerRepo.updatePeerInfo(peer);
                        requestStatus.setUserID(user.getUserID());
                        requestStatus.setAccountType(2);
                        requestStatus.setStatus(error);
                    }
                } else if (msg.getTitle().equals("PWChange")) {
                    //implement password change logic
                } else if (msg.getTitle().equals("Logout")) {
                    requestStatus.setTitle("LogoutSuccess");
                    LogoutMessage logoutMessage = (LogoutMessage) msg;
                    Peer peer = peerRepo.getPeer(logoutMessage.getUserID());
                    if (peer != null) {
                        peer.setLastSeen(0);
                        peerRepo.updatePeerInfo(peer);
                    }
                    Main.decreaseLoggedInUsers();
                } else if (msg.getTitle().equals("HeartBeat")) {
                    HeartBeatMessage heartBeatMessage = (HeartBeatMessage) msg;
                    Peer peer = new Peer(heartBeatMessage.getSenderID(), heartBeatMessage.getSenderAddress()
                            , heartBeatMessage.getSenderPort());
                    peer.setLastSeen(heartBeatMessage.getTimestamp());
                    peerRepo.updatePeerInfo(peer);
                }
                os.writeObject(requestStatus);
                os.flush();
                os.close();
                is.close();
                senderSocket.close();
            } else {
                System.out.println("invalid message");
            }
            senderSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Peer> getOnlinePeerList() {
        ArrayList<Peer> peers = new ArrayList<>();
        int loggedInUsers = Main.getLoggedInUsers();
        if (loggedInUsers > 30) {
            return peerRepo.getPeerList(loggedInUsers/6);
        } else if (loggedInUsers > 10) {
            return peerRepo.getPeerList(loggedInUsers/5);
        } else if (loggedInUsers > 5){
            return peerRepo.getPeerList(3);
        } else {
            return peerRepo.getPeerList(loggedInUsers);
        }
    }
}
