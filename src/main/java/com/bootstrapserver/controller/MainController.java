package com.bootstrapserver.controller;

import com.bootstrapserver.model.User;
import com.bootstrapserver.repository.UserRepository;
import com.bootstrapserver.util.Main;
import com.bootstrapserver.util.UIUpdater;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, UIUpdater {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> userIDCol;

    @FXML
    private TableColumn<User, String> usernameCol;

    @FXML
    private TableColumn<User, Integer> accessLevelCol;

    @FXML
    private MenuButton btnSettings;

    @FXML
    private MenuItem changePassword;

    @FXML
    private MenuItem btnLogout;

    private ObservableList<User> users;

    @FXML
    void updateAccessLevel(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       /* changePassword.setOnAction(MouseEvent -> {

        } );

        btnLogout.setOnAction(MouseEvent -> {

        });*/

        Main.setRegistrationListener(this);

        UserRepository userRepository = UserRepository.getUserRepository();
        users = FXCollections.observableArrayList(userRepository.getUsers());

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

        accessLevelCol.setCellValueFactory(new PropertyValueFactory<>("accessLevel"));
        accessLevelCol.setCellFactory(ComboBoxTableCell.forTableColumn((ObservableList) FXCollections.observableArrayList(1, 2)));
        accessLevelCol.setOnEditCommit(
                (TableColumn.CellEditEvent<User, Integer> t) -> {
                    User user = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    user.setAccessLevel(t.getNewValue());
                    userRepository.updateUser(user);
                });
        userTable.setItems(users);
    }

    @Override
    public void updateUI(User user) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                users.add(user);
            }
        });
    }
}
