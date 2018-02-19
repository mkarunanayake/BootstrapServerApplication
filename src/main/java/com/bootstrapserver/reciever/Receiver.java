/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootstrapserver.reciever;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import message.LoginMessage;
import message.RequestStatusMessage;
import message.Message;
import message.RegisterMessage;
import com.bootstrapserver.model.Peer;
import com.bootstrapserver.model.User;
import com.bootstrapserver.repository.PeerRepository;
import com.bootstrapserver.repository.UserRepository;
import com.bootstrapserver.util.Main;
import com.bootstrapserver.validator.MessageValidator;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

/**
 *
 * @author Mevan
 */
public class Receiver implements Runnable{

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
            System.out.println("Message Received");
            if (messageValidator.validate(msg)){
                System.out.println("inside validator");
                String error = "Success";
                RequestStatusMessage requestStatus = new RequestStatusMessage();
                if (msg.getTitle().equals("Login")){
                    System.out.println("InsideLogin");
                    requestStatus.setTitle("LoginStatus");
                    LoginMessage loginMsg= (LoginMessage)msg;
                    User user = userRepo.getUser(loginMsg.getUsername());
                    if (user == null){
                        error = "Invalid user details!";
                    } else if (!user.getPassword().equals(loginMsg.getPassword())){
                        error = "Invalid user details!";
                    } else {
                        Peer peer = new Peer(user.getUserID(), loginMsg.getSenderAddress(), loginMsg.getSenderPort());
                        peerRepo.updatePeerInfo(peer);

                    }
                    requestStatus.setStatus(error);
                } else if (msg.getTitle().equals("Register")){
                    RegisterMessage regMsg = (RegisterMessage) msg;
                    requestStatus.setTitle("RegisterStatus");
                    if (userRepo.getUser(regMsg.getUsername()) != null) {
                        error = "Username Already Taken!";
                        requestStatus.setStatus(error);
                    }else {
                        System.out.println("valid account");
                        User user = new User(Main.giveUserID(), regMsg.getUsername(), regMsg.getPassword(), 2 );
                        userRepo.saveUser(user);
                        Peer peer = new Peer(user.getUserID(), regMsg.getSenderAddress(), regMsg.getSenderPort());
                        peerRepo.updatePeerInfo(peer);
                        requestStatus.setUserID(user.getUserID());
                        requestStatus.setAccountType(2);
                        requestStatus.setStatus(error);
                        System.out.println("state written");
                    }
                }else if (msg.getTitle().equals("PWChange")){
                    //implement password change logic
                }else if (msg.getTitle().equals("Logout")){
                    //implement logout logic
                }
                System.out.println(requestStatus.getTitle());
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
}
