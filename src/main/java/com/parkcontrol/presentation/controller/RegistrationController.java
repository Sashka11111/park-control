package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.security.PasswordHashing;
import com.parkcontrol.domain.validation.UserValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.entity.UserRole;
import com.parkcontrol.persistence.repository.contract.UserRepository;
import com.parkcontrol.persistence.repository.impl.UserRepositoryImpl;
import com.parkcontrol.presentation.animation.Shake;
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
    btnClose.setOnAction(event -> {
      System.exit(0);
    });
    SignUpHyperlink.setOnAction(event -> {
      Scene currentScene = SignUpHyperlink.getScene();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));
      try {
        Parent root = loader.load();
        currentScene.setRoot(root);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    SignInButton.setOnAction(event -> {
      String username = loginField.getText();
      String password = passwordField.getText();
      if (username.isEmpty() || password.isEmpty()) {
        errorMessageLabel.setText("Логін та пароль не повинен бути пустим");
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
        return;
      }
      if (UserValidator.isUsernameValid(username) && UserValidator.isPasswordValid(password)) {
        if (!userRepository.isUsernameExists(username)) {
          // Хешування пароля
          String hashedPassword = PasswordHashing.getInstance().hashedPassword(password);
          UserRole role = UserRole.USER;

          // Створення нового користувача
          User user = new User(0, username, hashedPassword, role);
          // Додавання користувача до бази даних через UserRepository
          userRepository.addUser(user);

          Scene currentScene = SignUpHyperlink.getScene();
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));
          try {
            Parent root = loader.load();
            currentScene.setRoot(root);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        } else {
          errorMessageLabel.setText("Логін з ім'ям " + username + " уже існує");
          Shake userLoginAnim = new Shake(loginField);
          userLoginAnim.playAnim();
        }
      } else {
        errorMessageLabel.setText("Пароль має мати велику, маленьку букву та цифру.\n" + "Мінімальна довжина паролю: 6 символів");
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
      }
    });
  }
}