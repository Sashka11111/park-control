package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.domain.security.PasswordHashing;
import com.parkcontrol.domain.validation.AuthenticatedUser;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.repository.contract.UserRepository;
import com.parkcontrol.persistence.repository.impl.UserRepositoryImpl;
import com.parkcontrol.presentation.animation.Shake;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AuthorizationController {

  @FXML
  private Hyperlink authSignInHyperlink;

  @FXML
  private Button authSingUpButton;

  @FXML
  private TextField loginField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button btn_close;
  @FXML
  private Label errorMessageLabel;
  @FXML
  private Label medGuide;
  @FXML
  private Label authLabel;

  @FXML
  private Label authQuestion;
  private UserRepository userRepository; // Змінна для зберігання UserRepository

  // Параметризований конструктор, який приймає userRepository
  public AuthorizationController() {
    this.userRepository = new UserRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    btn_close.setOnAction(event -> {
      System.exit(0);
    });
    authSignInHyperlink.setOnAction(event -> {
      // Отримуємо сцену з гіперпосилання
      Scene currentScene = authSignInHyperlink.getScene();
      // Завантажуємо нову сцену з файлу registration.fxml
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/registration.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        // Встановлюємо нову сцену на поточному вікні
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(newScene);
        primaryStage.show();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });


    authSingUpButton.setOnAction(event -> {
      String loginText = loginField.getText().trim();
      String loginPassword = passwordField.getText().trim();

      if (!loginText.isEmpty() && !loginPassword.isEmpty()) {
        try {
          // Перевірка логіну та пароля користувача
          User user = userRepository.findByUsername(loginText);
          if (user != null) {
            // Хешування введеного пароля
            String hashedPassword = PasswordHashing.getInstance().hashedPassword(loginPassword);
            if (user.password().equals(hashedPassword)) {
              AuthenticatedUser.getInstance().setCurrentUser(user);
              authSingUpButton.getScene().getWindow().hide();
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mainMenu.fxml"));
              Parent root = loader.load();
              Stage stage = new Stage();
              stage.getIcons().add(new Image(getClass().getResourceAsStream("/data/icon.png")));
              stage.setScene(new Scene(root));
              stage.initStyle(StageStyle.UNDECORATED);
              stage.showAndWait();
            }
          }
        } catch (EntityNotFoundException e) {
          throw new RuntimeException(e);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  private void showError(String message) {
    errorMessageLabel.setText(message);
    Shake userLoginAnim = new Shake(loginField);
    Shake userPassAnim = new Shake(passwordField);
    userLoginAnim.playAnim();
    userPassAnim.playAnim();
  }

}