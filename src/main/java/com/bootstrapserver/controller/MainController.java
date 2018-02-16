package com.bootstrapserver.controller;

import com.bootstrapserver.message.Message;
import com.bootstrapserver.util.Main;
import com.bootstrapserver.util.UIUpdater;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements UIUpdater, Initializable{

    @FXML
    private TableView<?> userTable;

    @FXML
    private TableColumn<?, ?> usernameCol;

    @FXML
    private TableColumn<?, ?> peerAddCol;

    @FXML
    private TableColumn<?, ?> peerPortCol;

    @FXML
    private TableColumn<?, ?> accessLvlCol;

    @FXML
    private TableColumn<?, ?> lastSeenCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addRegistrationListener(this);
    }

    @FXML
    void updateAccessLevel(ActionEvent event) {

    }

    @Override
    public void updateUI(Message message) {

    }

}
