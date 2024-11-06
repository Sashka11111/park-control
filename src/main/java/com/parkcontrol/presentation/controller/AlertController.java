package com.parkcontrol.presentation.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertController {

  @FXML
  private Label messageLabel;
  @FXML
  private Button closeButton;

  private Stage stage;
  @FXML
  void initialize() {
    closeButton.setOnAction(event -> stage.close());
  }
  @FXML
  private void handleOkAction() {
    stage.close();
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void setMessage(String message) {
    messageLabel.setText(message);
  }


  public static void showAlert( String message) {
    try {
      FXMLLoader loader = new FXMLLoader(AlertController.class.getResource("/view/alert.fxml"));
      AnchorPane root = loader.load();
      AlertController controller = loader.getController();
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.UNDECORATED);
      Scene scene = new Scene(root);
      stage.setScene(scene);
      controller.setStage(stage);
      controller.setMessage(message);
      stage.showAndWait();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}