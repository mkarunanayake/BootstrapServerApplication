package com.bootstrapserver.util;

import com.bootstrapserver.model.User;
import com.bootstrapserver.reciever.ReceiverHandler;
import com.bootstrapserver.repository.PeerRepository;
import com.bootstrapserver.repository.UserRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Main extends Application{
    private static int userID;

    private static PropertiesConfiguration prop;
    private static ArrayList<UIUpdater> registrationListeners = new ArrayList<>();
    public static void main(String[] args){

        UserRepository userRepo = new UserRepository();
        userRepo.setupUserTable();
        PeerRepository peerRepo = new PeerRepository();
        peerRepo.setupPeerTable();
        try {
            prop = new PropertiesConfiguration("properties/user.properties");
            userID = Integer.parseInt(String.valueOf(prop.getProperty("user_id")));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println(userID);
        int port = 25025;
        if (args.length>1){
            port = Integer.parseInt(args[1]);
        }
        ReceiverHandler receiverHandler = new ReceiverHandler(port);
        Thread t = new Thread(receiverHandler);
        t.start();

    }

    public static void addRegistrationListener(UIUpdater uiUpdater){
        registrationListeners.add(uiUpdater);
    }

    public static void removeRegistrationListener(UIUpdater uiUpdater) {
        if (registrationListeners.contains(uiUpdater)){
            registrationListeners.remove(uiUpdater);
        }
    }

    public static ArrayList<UIUpdater> getRegistrationListeners(){
        return registrationListeners;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        Scene scene = new Scene(parent, 1024, 768);
        setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Main");
        primaryStage.show();
    }

    public static int giveUserID(){
        Main.userID +=1;
        synchronized (prop){
            prop.setProperty("user_id", String.valueOf(userID));
            try {
                prop.save();
                System.out.println("saved");
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        System.out.println(userID);
        return userID;
    }

}
