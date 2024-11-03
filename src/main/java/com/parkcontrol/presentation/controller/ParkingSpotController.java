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
  private TableColumn<ParkingSpot, String> levelColumn;

  @FXML
  private TextField levelField;

  @FXML
  private TableView<ParkingSpot> parkingSpotTable;

  @FXML
  private TableColumn<ParkingSpot, String> sectionColumn;

  @FXML
  private TextField sectionField;

  @FXML
  private TextField searchTextField;

  @FXML
  private TableColumn<ParkingSpot, String> sizeColumn;

  @FXML
  private ComboBox<String> sizeComboBox;

  @FXML
  private TableColumn<ParkingSpot, String> spotNumberColumn;

  @FXML
  private TextField spotNumberField;

  @FXML
  private TableColumn<ParkingSpot, String> statusColumn;

  @FXML
  private ComboBox<String> statusComboBox;

  private List<ParkingSpot> parkingSpots;
  private ParkingSpotRepositoryImpl parkingSpotRepository;

  public ParkingSpotController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().section()));
    levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().level())));
    spotNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().spotNumber()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));

    sizeComboBox.getItems().addAll("Стандартне", "Велике", "Для інвалідів");
    statusComboBox.getItems().addAll("Вільне", "Зайняте", "Зарезервоване");

    loadParkingSpots();

    parkingSpotTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showParkingSpotDetails(newValue));

    clearFieldsButton.setOnAction(event -> clearFields());
    addButton.setOnAction(event -> addParkingSpot());
    editButton.setOnAction(event -> editParkingSpot());
    deleteButton.setOnAction(event -> deleteParkingSpot());
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterParkingSpots(newValue));
  }

  private void loadParkingSpots() {
    parkingSpots = parkingSpotRepository.findAll();
    parkingSpotTable.setItems(FXCollections.observableArrayList(parkingSpots));
  }

  private void showParkingSpotDetails(ParkingSpot parkingSpot) {
    if (parkingSpot != null) {
      sectionField.setText(parkingSpot.section());
      levelField.setText(String.valueOf(parkingSpot.level()));
      spotNumberField.setText(parkingSpot.spotNumber());
      statusComboBox.setValue(parkingSpot.status());
      sizeComboBox.setValue(parkingSpot.size());
    } else {
      clearFields();
    }
  }

  @FXML
  private void addParkingSpot() {
    try {
      String section = sectionField.getText().trim();
      int level = Integer.parseInt(levelField.getText().trim());
      String spotNumber = spotNumberField.getText().trim();
      String status = statusComboBox.getValue();
      String size = sizeComboBox.getValue();

      ParkingSpot newParkingSpot = new ParkingSpot(0, section, level, spotNumber, status, size);
      List<ParkingSpot> existingParkingSpots = parkingSpotRepository.findAll();
      String validationMessage = ParkingSpotValidator.validateParkingSpot(newParkingSpot, existingParkingSpots);

      if (validationMessage == null) {
        parkingSpotRepository.addParkingSpot(newParkingSpot);
        loadParkingSpots();
        clearFields();
      } else {
        AlertController.showAlert(validationMessage);
      }
    } catch (NumberFormatException e) {
      AlertController.showAlert("Поверх повинен бути числом.");
    } catch (Exception e) {
      AlertController.showAlert("Не вдалося зберегти нове паркувальне місце: " + e.getMessage());
    }
  }
  @FXML
  private void editParkingSpot() {
    ParkingSpot selectedParkingSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
    if (selectedParkingSpot != null) {
      try {
        String section = sectionField.getText().trim();
        int level = Integer.parseInt(levelField.getText().trim());
        String spotNumber = spotNumberField.getText().trim();
        String status = statusComboBox.getValue();
        String size = sizeComboBox.getValue();

        ParkingSpot updatedParkingSpot = new ParkingSpot(
            selectedParkingSpot.spotId(),
            section,
            level,
            spotNumber,
            status,
            size
        );

        String validationMessage = ParkingSpotValidator.validateParkingSpot(updatedParkingSpot, parkingSpots);
        if (validationMessage == null) {
          parkingSpotRepository.updateParkingSpot(updatedParkingSpot);
          loadParkingSpots();
          clearFields();
        } else {
          AlertController.showAlert(validationMessage);
        }
      } catch (NumberFormatException e) {
        AlertController.showAlert("Поверх повинен бути числом.");
      } catch (Exception e) {
        AlertController.showAlert("Не вдалося оновити паркувальне місце: " + e.getMessage());
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
  private void filterParkingSpots(String searchText) {
    // Якщо searchText порожній, відновіть повний список
    if (searchText.isEmpty()) {
      parkingSpotTable.setItems(FXCollections.observableArrayList(parkingSpots));
      return;
    }

    // Фільтруємо паркувальні місця
    List<ParkingSpot> filteredList = parkingSpots.stream()
        .filter(spot -> {
          String section = spot.section().toLowerCase();
          String spotNumber = spot.spotNumber().toLowerCase();
          String status = spot.status().toLowerCase();
          String size = spot.size().toLowerCase();
          int level = spot.level(); // Якщо потрібно, можна також конвертувати рівень в строку, якщо це потрібно

          // Повертаємо true, якщо будь-яке поле містить searchText
          return section.contains(searchText.toLowerCase()) ||
              spotNumber.contains(searchText.toLowerCase()) ||
              status.contains(searchText.toLowerCase()) ||
              size.contains(searchText.toLowerCase()) ||
              String.valueOf(level).contains(searchText); // Пошук по рівню
        })
        .toList();
    parkingSpotTable.setItems(FXCollections.observableArrayList(filteredList));

    if (filteredList.isEmpty()) {
      parkingSpotTable.setPlaceholder(new Label("На жаль таких даних немає"));
    }

  }


  @FXML
  private void clearFields() {
    sectionField.clear();
    levelField.clear();
    spotNumberField.clear();
    sizeComboBox.setValue(null);
    statusComboBox.setValue(null);
  }
}
