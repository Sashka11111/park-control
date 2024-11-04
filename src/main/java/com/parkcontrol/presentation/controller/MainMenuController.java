package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.security.AuthenticatedUser;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.entity.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenuController {

  @FXML
  private Button reservationButton;

  @FXML
  private Button parkingSpotsButton;

  @FXML
  private Button myReservationButton;

  @FXML
  private Button closeButton;

  @FXML
  private Button minimazeButton;

  @FXML
  private Button usersManagementButton;

  @FXML
  private Button changeAccountButton;


  @FXML
  private StackPane stackPane;

  @FXML
  private StackPane contentArea;

  @FXML
  private Label userName;

  private Stage stage;
  private double xOffset = 0;
  private double yOffset = 0;
  @FXML
  void initialize() {
    closeButton.setOnAction(event -> System.exit(0));
    minimazeButton.setOnAction(event -> minimizeWindow());
    Reservation();
    reservationButton.setOnAction(event -> showReservationPage());
    myReservationButton.setOnAction(event -> showMyReservationPage());
    parkingSpotsButton.setOnAction(event -> showParkingSpotsPage());
    usersManagementButton.setOnAction(event -> showUsersPage());
    changeAccountButton.setOnAction(event -> handleChangeAccountAction());

    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();

    userName.setText(currentUser.username());

    if (currentUser.role() != UserRole.ADMIN) {
      parkingSpotsButton.setVisible(false);
      usersManagementButton.setVisible(false);
    }

    Platform.runLater(() -> {
      Stage primaryStage = (Stage) contentArea.getScene().getWindow();
      addDragListeners(primaryStage.getScene().getRoot());
    });
  }
//
  private void moveStackPane(Button button) {
    double buttonX = button.localToParent(button.getBoundsInLocal()).getMinX();
    double buttonY = button.localToParent(button.getBoundsInLocal()).getMinY();
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), stackPane);
    transition.setToX(buttonX);
    stackPane.setLayoutY(buttonY);
  }

  private void showParkingSpotsPage() {
    moveStackPane(parkingSpotsButton);
    loadFXML("/view/parkingSpot.fxml");
  }

  private void showReservationPage() {
    moveStackPane(reservationButton);
    loadFXML("/view/reservation.fxml");
  }
  private void showMyReservationPage() {
    moveStackPane(myReservationButton);
    loadFXML("/view/myReservation.fxml");
  }

  private void showUsersPage() {
    moveStackPane(usersManagementButton);
    loadFXML("/view/usersManagement.fxml");
  }

  private void loadFXML(String fxmlFileName) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
      Parent fxml = loader.load();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(fxml);
    } catch (IOException ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void Reservation() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservation.fxml"));
      AnchorPane bookingsAnchorPane = loader.load();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(bookingsAnchorPane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void minimizeWindow() {
    if (stage == null) {
      stage = (Stage) minimazeButton.getScene().getWindow();
    }
    stage.setIconified(true);
  }

  private void addDragListeners(Parent root) {
    root.setOnMousePressed(event -> {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    });

    root.setOnMouseDragged(event -> {
      Stage stage = (Stage) ((Parent) event.getSource()).getScene().getWindow();
      stage.setX(event.getScreenX() - xOffset);
      stage.setY(event.getScreenY() - yOffset);
    });
  }
  private void handleChangeAccountAction() {
    try {
      // Анімація для поточного вікна
      Stage currentStage = (Stage) changeAccountButton.getScene().getWindow();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentStage.getScene().getRoot());
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      fadeOut.setOnFinished(event -> {
        try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));
          Parent root = loader.load();

          // Анімація для нового вікна
          Stage loginStage = new Stage();
          loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/data/icon.png")));
          loginStage.initStyle(StageStyle.UNDECORATED);
          Scene scene = new Scene(root);
          scene.getRoot().setOpacity(0.0);
          loginStage.setScene(scene);
          loginStage.show();

          FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
          fadeIn.setFromValue(0.0);
          fadeIn.setToValue(1.0);
          fadeIn.play();

          // Закриття поточного вікна
          currentStage.close();
        } catch (IOException ex) {
          Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
      });
      fadeOut.play();
    } catch (Exception ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}