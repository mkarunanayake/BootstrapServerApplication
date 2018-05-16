package com.bootstrapserver.controller;

import com.bootstrapserver.model.User;
import com.bootstrapserver.repository.SystemUserRepository;
import com.bootstrapserver.repository.UserRepository;
import com.bootstrapserver.util.Main;
import com.bootstrapserver.util.PasswordEncrypter;
import com.bootstrapserver.util.SystemUser;
import com.bootstrapserver.util.UIUpdater;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import messenger.ServerHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
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

    @FXML
    private Label statusLabel;

    private ObservableList<User> users;

    @FXML
    void updateAccessLevel(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.setText("BS running on: " + ServerHandler.getIpAddress().getHostAddress() + "/" + ServerHandler.getPort());
        changePassword.setOnAction(MouseEvent -> {
            Dialog<String> changePasswordDialog = new Dialog<>();
            changePasswordDialog.setTitle("Change Password");
            changePasswordDialog.setHeaderText("Provide a password between 8-20 characters");
            Label lblOld = new Label("Old Password ");
            Label lblNew = new Label("New Password ");
            Label lblConfirm = new Label("Confirm Password ");
            PasswordField oldPassword = new PasswordField();
            PasswordField newPassword = new PasswordField();
            PasswordField confirmPassword = new PasswordField();

            GridPane gridPane = new GridPane();
            gridPane.add(lblOld, 1, 1);
            gridPane.add(new Label(" : "), 2, 1);
            gridPane.add(oldPassword, 3, 1);
            gridPane.add(lblNew, 1, 2);
            gridPane.add(new Label(" : "), 2, 2);
            gridPane.add(newPassword, 3, 2);
            gridPane.add(lblConfirm, 1, 3);
            gridPane.add(new Label(" : "), 2, 3);
            gridPane.add(confirmPassword, 3, 3);
            gridPane.setVgap(10);
            changePasswordDialog.getDialogPane().setContent(gridPane);

            ButtonType okButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            changePasswordDialog.getDialogPane().getButtonTypes().add(0, okButton);
            changePasswordDialog.getDialogPane().getButtonTypes().add(1, cancelButton);

            while (true) {
                changePasswordDialog.setResultConverter(new Callback<ButtonType, String>() {
                    @Override
                    public String call(ButtonType param) {
                        String error = "error";
                        if (param == okButton) {
                            try {
                                String oldPW = oldPassword.getText().trim();
                                String newPW = newPassword.getText().trim();
                                String confirmPW = confirmPassword.getText().trim();
                                if (PasswordEncrypter.SHA1(oldPW).equals(SystemUser.getPassword())) {
                                    if (newPW.length() >= 8 && newPW.length() <= 20) {
                                        if (newPW.equals(confirmPW)) {
                                            error = PasswordEncrypter.SHA1(newPW);
                                        } else {
                                            changePasswordDialog.setHeaderText("Password didn't match!");
                                        }
                                    }
                                } else {
                                    changePasswordDialog.setHeaderText("Invalid Old Password");
                                }
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else if (param == cancelButton) {
                            error = "cancel";
                        }
                        return error;
                    }
                });

                Optional<String> result = changePasswordDialog.showAndWait();
                if (result.isPresent() && result.get().equals("cancel")) {
                    break;
                } else if (result.isPresent() && (!result.get().equals("error"))) {
                    SystemUserRepository.getSystemUserRepository().updateSystemUserPassword(result.get());
                    SystemUserRepository.getSystemUserRepository().getSystemUser();
                    System.out.println(result.get());
                    System.out.println("Changed");
                    break;
                }
            }
        });

        btnLogout.setOnAction(MouseEvent -> {
            Stage stage = (Stage) btnSettings.getScene().getWindow();
            Parent parent = null;
            try {
                parent = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(parent, 1024, 768);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        });

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
