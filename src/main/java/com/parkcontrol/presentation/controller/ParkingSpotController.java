package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.validation.ParkingSpotValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class ParkingSpotController {

  @FXML
  private Button addButton;

  @FXML
  private Button clearFieldsButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button editButton;

  @FXML
  private TextArea locationArea;

  @FXML
  private TableColumn<ParkingSpot, String> locationColumn;

  @FXML
  private Label locationLabel;

  @FXML
  private TableView<ParkingSpot> parkingSpotTable;

  @FXML
  private ComboBox<String> sizeComboBox;

  @FXML
  private Label sizeLabel;

  @FXML
  private TableColumn<ParkingSpot, String> sizeColumn;

  @FXML
  private TableColumn<ParkingSpot, String> statusColumn;

  @FXML
  private ComboBox<String> statusComboBox;

  @FXML
  private Label statusLabel;

  private List<ParkingSpot> parkingSpots = FXCollections.observableArrayList();
  private ParkingSpotRepositoryImpl parkingSpotRepository;

  public ParkingSpotController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().location()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));

    sizeComboBox.getItems().addAll("Стандартне", "Велике", "Для інвалідів");
    statusComboBox.getItems().addAll("Вільне", "Зайняте");

    loadParkingSpots();

    parkingSpotTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showParkingSpotDetails(newValue));

    clearFieldsButton.setOnAction(event -> clearFields());
    addButton.setOnAction(event -> addParkingSpot());
    editButton.setOnAction(event -> editParkingSpot());
    deleteButton.setOnAction(event -> deleteParkingSpot());
  }

  private void loadParkingSpots() {
    parkingSpots.clear();
    parkingSpots.addAll(parkingSpotRepository.findAll());
    parkingSpotTable.setItems(FXCollections.observableArrayList(parkingSpots));
  }

  private void showParkingSpotDetails(ParkingSpot parkingSpot) {
    if (parkingSpot != null) {
      locationArea.setText(parkingSpot.location());
      statusComboBox.setValue(parkingSpot.status());
      sizeComboBox.setValue(parkingSpot.size());
    } else {
      clearFields();
    }
  }

  @FXML
  private void addParkingSpot() {
    String location = locationArea.getText().trim();
    String status = statusComboBox.getValue();
    String size = sizeComboBox.getValue();

    ParkingSpot newParkingSpot = new ParkingSpot(0, location, status, size);

    List<ParkingSpot> existingParkingSpots = parkingSpotRepository.findAll();
    String validationMessage = ParkingSpotValidator.validateParkingSpot(newParkingSpot, existingParkingSpots);

    if (validationMessage == null) {
      try {
        parkingSpotRepository.addParkingSpot(newParkingSpot);
        loadParkingSpots();
        clearFields();
      } catch (Exception e) {
        AlertController.showAlert("Не вдалося зберегти нове паркувальне місце: " + e.getMessage());
      }
    } else {
      AlertController.showAlert(validationMessage);
    }
  }

  @FXML
  private void editParkingSpot() {
    ParkingSpot selectedParkingSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
    if (selectedParkingSpot != null) {
      String location = locationArea.getText().trim();
      String status = statusComboBox.getValue();
      String size = sizeComboBox.getValue();

      ParkingSpot updatedParkingSpot = new ParkingSpot(selectedParkingSpot.spotId(), location, status, size);

      String validationMessage = ParkingSpotValidator.validateParkingSpot(updatedParkingSpot, parkingSpots);
      if (validationMessage == null) {
        try {
          parkingSpotRepository.updateParkingSpot(updatedParkingSpot);
          loadParkingSpots();
          clearFields();
        } catch (Exception e) {
          AlertController.showAlert("Не вдалося оновити паркувальне місце: " + e.getMessage());
        }
      } else {
        AlertController.showAlert(validationMessage);
      }
    } else {
      AlertController.showAlert("Оберіть паркувальне місце для редагування.");
    }
  }

  @FXML
  private void deleteParkingSpot() {
    ParkingSpot selectedParkingSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
    if (selectedParkingSpot != null) {
      try {
        parkingSpotRepository.deleteParkingSpot(selectedParkingSpot.spotId());
        loadParkingSpots();
        clearFields();
      } catch (Exception e) {
        AlertController.showAlert("Не вдалося видалити паркувальне місце: " + e.getMessage());
      }
    } else {
      AlertController.showAlert("Оберіть паркувальне місце для видалення.");
    }
  }

  @FXML
  private void clearFields() {
    locationArea.clear();
    sizeComboBox.setValue(null);
    statusComboBox.setValue(null);
  }
}
