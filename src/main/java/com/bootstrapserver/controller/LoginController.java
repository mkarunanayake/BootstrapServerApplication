package com.bootstrapserver.controller;

import com.bootstrapserver.util.PasswordEncrypter;
import com.bootstrapserver.util.SystemUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private Button btnLogin;

    @FXML
    private Label statusLabel;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void confirmLogin(MouseEvent event) throws IOException, NoSuchAlgorithmException {
        String username = txtUsername.getText().trim();
        String password = PasswordEncrypter.SHA1(txtPassword.getText().trim());

        if (!username.equals(SystemUser.getUsername())) {
            statusLabel.setText("Invalid Username!");
        } else if (!password.equals(SystemUser.getPassword())) {
            statusLabel.setText("Invalid Password!");
        } else {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Parent parent = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
            Scene scene = new Scene(parent, 1024, 768);
            stage.setScene(scene);
            stage.setTitle("Main");
            stage.show();
        }

    }

}
