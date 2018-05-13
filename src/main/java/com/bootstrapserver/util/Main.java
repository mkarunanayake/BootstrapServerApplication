package com.bootstrapserver.util;

import com.bootstrapserver.repository.SystemUserRepository;
import com.bootstrapserver.repository.TableRepository;
import com.bootstrapserver.repository.UserRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import messenger.OnlinePeerHandler;
import messenger.ReceiverHandler;
import messenger.ServerHandler;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Main extends Application {
    private static int userID;

    private static UIUpdater registrationListener;

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        TableRepository.createTables();
        Main.userID = UserRepository.getUserRepository().getLastUserID();
        SystemUserRepository.getSystemUserRepository().getSystemUser();
        System.out.println(Main.userID);

        int port = 25025;
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        ServerHandler.setPort(port);
        ServerHandler.getLocalIPAddress();
        ReceiverHandler receiverHandler = new ReceiverHandler(port);
        Thread t = new Thread(receiverHandler);
        t.start();
        OnlinePeerHandler.startHandler();
        launch(args);
    }


    public static UIUpdater getRegistrationListener() {
        return registrationListener;
    }

    public static void setRegistrationListener(UIUpdater registrationListener) {
        Main.registrationListener = registrationListener;
    }

    public static int giveUserID() {
        Main.userID += 1;
        return userID;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(parent, 1024, 768);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

}
