/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messenger;

import com.bootstrapserver.model.User;
import com.bootstrapserver.repository.PeerRepository;
import com.bootstrapserver.repository.UserRepository;
import com.bootstrapserver.util.Main;
import com.bootstrapserver.util.UIUpdater;
import com.bootstrapserver.validator.MessageValidator;
import message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * @author Mevan
 */
public class Receiver implements Runnable {

    private final Socket senderSocket;
    private MessageValidator messageValidator;
    private UserRepository userRepo;
    private PeerRepository peerRepo;

    Receiver(Socket senderSocket) throws SQLException {
        this.senderSocket = senderSocket;
        messageValidator = MessageValidator.getMessageValidator();
        userRepo = UserRepository.getUserRepository();
        peerRepo = PeerRepository.getPeerRepository();
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
                        Peer peer = new Peer(user.getUserID(), loginMsg.getSenderAddress(), loginMsg.getSenderPort());
                        peer.setLastSeen(loginMsg.getTimestamp());
                        requestStatus.setUserID(user.getUserID());
                        requestStatus.setAccountType(user.getAccessLevel());
                        requestStatus.setLastSeen(peerRepo.getPeer(user.getUserID()).getLastSeen());
                        requestStatus.setActivePeers(OnlinePeerHandler.getOnlinePeers(user.getUserID()));
                    }
                    requestStatus.setStatus(error);
                } else if (msg.getTitle().equals("Register")) {
                    RegisterMessage regMsg = (RegisterMessage) msg;
                    requestStatus.setTitle("RegisterStatus");
                    if (userRepo.getUser(regMsg.getUsername()) != null) {
                        error = "Username Already Taken!";
                    } else {
                        User user = new User(Main.giveUserID(), regMsg.getUsername(), regMsg.getPassword(), 2);
                        userRepo.saveUser(user);
                        UIUpdater regListener = Main.getRegistrationListener();
                        if (regListener != null) {
                            regListener.updateUI(user);
                        }
                        Peer peer = new Peer(user.getUserID(), regMsg.getSenderAddress(), regMsg.getSenderPort());
                        peer.setLastSeen(0);
                        peerRepo.updatePeerInfo(peer);
                        requestStatus.setUserID(user.getUserID());
                        requestStatus.setAccountType(2);
                        requestStatus.setLastSeen(0);
                        requestStatus.setActivePeers(OnlinePeerHandler.getOnlinePeers(user.getUserID()));
                    }
                    requestStatus.setStatus(error);
                } else if (msg.getTitle().equals("PWChange")) {
                    requestStatus.setTitle("PWChangeStatus");
                    //implement password change logic
                } else if (msg.getTitle().equals("Logout")) {
                    requestStatus.setTitle("LogoutSuccess");
                    LogoutMessage logoutMessage = (LogoutMessage) msg;
                    Peer peer = new Peer(logoutMessage.getUserID(), logoutMessage.getSenderAddress(), logoutMessage.getSenderPort());
                    peer.setLastSeen(logoutMessage.getTimestamp());
                    OnlinePeerHandler.userLogout(peer);
                } else if (msg.getTitle().equals("HeartBeatMessage")) {
                    requestStatus.setTitle("HeartBeatSuccess");
                    HeartBeatMessage heartBeatMessage = (HeartBeatMessage) msg;
                    Peer peer = new Peer(heartBeatMessage.getSenderID(), heartBeatMessage.getSenderAddress()
                            , heartBeatMessage.getSenderPort());
                    peer.setLastSeen(heartBeatMessage.getTimestamp());
                    OnlinePeerHandler.heartbeatRecieved(peer);
                }
                os.writeObject(requestStatus);
                os.flush();
                os.close();
                is.close();
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
