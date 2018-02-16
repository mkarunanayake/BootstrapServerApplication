package com.bootstrapserver.util;

import com.bootstrapserver.reciever.ReceiverHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Main extends Application{
    private static int userID;
    private static Properties prop = new Properties();
    private static ArrayList<UIUpdater> registrationListeners = new ArrayList<>();
    public static void main(String[] args){
        ClassLoader loader= Thread.currentThread().getContextClassLoader();
        InputStream stream= loader.getResourceAsStream("/properties/user.properties");
        try {
            prop.load(stream);
            userID = Integer.parseInt(prop.getProperty("user_id"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        }
        return userID;
    }

}
