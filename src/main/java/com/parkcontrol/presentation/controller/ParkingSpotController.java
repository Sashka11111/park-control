//package com.parkcontrol.presentation.controller;
//
//import com.parkcontrol.domain.exception.EntityNotFoundException;
//import com.parkcontrol.persistence.entity.ParkingSpot;
//import com.parkcontrol.persistence.repository.contract.ParkingSpotRepository;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//
//import java.util.List;
//
//public class ParkingSpotController {
//
//  @FXML
//  private TableView<ParkingSpot> parkingSpotTable;
//  @FXML
//  private TableColumn<ParkingSpot, Integer> sizedColumn;
//  @FXML
//  private TableColumn<ParkingSpot, String> locationColumn;
//  @FXML
//  private TableColumn<ParkingSpot, String> statusColumn;
//  @FXML
//  private TextField locationTextField;
//  @FXML
//  private ComboBox<String> statusComboBox;
//  @FXML
//  private TextArea messageArea;
//
//  private ParkingSpotRepository parkingSpotRepository;
//  private ObservableList<ParkingSpot> parkingSpotList;
//
//  public ParkingSpotController(ParkingSpotRepository parkingSpotRepository) {
//    this.parkingSpotRepository = parkingSpotRepository;
//  }
//
//  @FXML
//  public void initialize() {
//    // Ініціалізація стовпців таблиці
//  //  spotIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().spotId()).asObject());
//    locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().location()));
//    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
//
//    // Заповнення ComboBox статусом
//    statusComboBox.getItems().addAll("FREE", "OCCUPIED");
//
//    // Завантаження даних у таблицю
//    loadParkingSpots();
//  }
//
//  private void loadParkingSpots() {
//    List<ParkingSpot> spots = parkingSpotRepository.findAll();
//    parkingSpotList = FXCollections.observableArrayList(spots);
//    parkingSpotTable.setItems(parkingSpotList);
//  }
//
//  @FXML
//  private void handleAddParkingSpot() {
//    String location = locationTextField.getText();
//    String status = statusComboBox.getValue();
//
//    if (location.isEmpty() || status == null) {
//      messageArea.setText("Заповніть всі поля!");
//      return;
//    }
//
//    ParkingSpot newSpot = new ParkingSpot(0, location, status);
//    parkingSpotRepository.addParkingSpot(newSpot);
//    messageArea.setText("Паркувальне місце додано!");
//
//    // Оновлення таблиці
//    loadParkingSpots();
//    clearFields();
//  }
//
//  @FXML
//  private void handleUpdateParkingSpot() {
//    ParkingSpot selectedSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
//    if (selectedSpot == null) {
//      messageArea.setText("Виберіть паркувальне місце для оновлення!");
//      return;
//    }
//
//    String location = locationTextField.getText();
//    String status = statusComboBox.getValue();
//
//    if (location.isEmpty() || status == null) {
//      messageArea.setText("Заповніть всі поля!");
//      return;
//    }
//
//    ParkingSpot updatedSpot = new ParkingSpot(selectedSpot.spotId(), location, status);
//    try {
//      parkingSpotRepository.updateParkingSpot(updatedSpot);
//      messageArea.setText("Паркувальне місце оновлено!");
//    } catch (EntityNotFoundException e) {
//      messageArea.setText(e.getMessage());
//    }
//
//    // Оновлення таблиці
//    loadParkingSpots();
//    clearFields();
//  }
//
//  @FXML
//  private void handleDeleteParkingSpot() {
//    ParkingSpot selectedSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
//    if (selectedSpot == null) {
//      messageArea.setText("Виберіть паркувальне місце для видалення!");
//      return;
//    }
//
//    try {
//      parkingSpotRepository.deleteParkingSpot(selectedSpot.spotId());
//      messageArea.setText("Паркувальне місце видалено!");
//    } catch (EntityNotFoundException e) {
//      messageArea.setText(e.getMessage());
//    }
//
//    // Оновлення таблиці
//    loadParkingSpots();
//    clearFields();
//  }
//
//  private void clearFields() {
//    locationTextField.clear();
//    statusComboBox.setValue(null);
//  }
//}
