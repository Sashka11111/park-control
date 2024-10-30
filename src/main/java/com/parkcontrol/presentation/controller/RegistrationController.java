package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.security.PasswordHashing;
import com.parkcontrol.domain.validation.UserValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.entity.UserRole;
import com.parkcontrol.persistence.repository.contract.UserRepository;
import com.parkcontrol.persistence.repository.impl.UserRepositoryImpl;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

  @FXML
  private Button SignInButton;

  @FXML
  private Hyperlink SignUpHyperlink;

  @FXML
  private Button btnClose;

  @FXML
  private Label errorMessageLabel;

  @FXML
  private TextField loginField;

  @FXML
  private PasswordField passwordField;

  private UserRepository userRepository;

  public RegistrationController() {
    this.userRepository = new UserRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    btnClose.setOnAction(event -> System.exit(0));

    SignUpHyperlink.setOnAction(event -> switchScene("/view/authorization.fxml"));

    SignInButton.setOnAction(event -> handleSignIn());
  }

  private void switchScene(String fxmlPath) {
    Scene currentScene = SignUpHyperlink.getScene();
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
    try {
      Parent root = loader.load();
      currentScene.setRoot(root);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleSignIn() {
    String username = loginField.getText();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
      displayError("Логін та пароль не повинен бути пустим");
      return;
    }

    if (UserValidator.isUsernameValid(username) && UserValidator.isPasswordValid(password)) {
      if (!userRepository.isUsernameExists(username)) {
        registerUser(username, password);
        switchScene("/view/authorization.fxml");
      } else {
        displayError("Логін з ім'ям " + username + " вже існує");
      }
    } else {
      displayError("Пароль має мати велику, маленьку букву та цифру(мінімум 6 символів).\nЛогін може містити лише латинські літери.");
    }
  }

  private void registerUser(String username, String password) {
    String hashedPassword = PasswordHashing.getInstance().hashedPassword(password);
    UserRole role = UserRole.USER;
    User user = new User(0, username, hashedPassword, role);
    userRepository.addUser(user);
  }

  private void displayError(String message) {
    AlertController.showAlert(message);
  }

}
