package com.parkcontrol.presentation.controller;

import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

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

  private List<ParkingSpot> parkingSpots=FXCollections.observableArrayList();;

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
  }

  private void loadParkingSpots() {
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
//    String location = locationArea.getText();
//    String size = sizeComboBox.getValue();
//    String status = statusComboBox.getValue();
//
//    ParkingSpot newSpot = new ParkingSpot(0,location, status, size);
//    parkingSpotRepository.addParkingSpot(newSpot);
//    parkingSpots.add(newSpot);
//    parkingSpotTable.getItems().add(newSpot);
//    clearFields();
  }

  @FXML
  private void editParkingSpot() {
//    ParkingSpot selectedSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
//    if (selectedSpot != null) {
//      selectedSpot.setLocation(locationArea.getText());
//      selectedSpot.setSize(sizeComboBox.getValue());
//      selectedSpot.setStatus(statusComboBox.getValue());
//      parkingSpotRepository.updateParkingSpot(selectedSpot);
//      parkingSpotTable.refresh();
//      clearFields();
//    }
  }

  @FXML
  private void deleteParkingSpot() {
//    ParkingSpot selectedSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
//    if (selectedSpot != null) {
//      parkingSpotRepository.deleteParkingSpot(selectedSpot.spotId());
//      parkingSpots.remove(selectedSpot);
//      parkingSpotTable.getItems().remove(selectedSpot);
//      clearFields();
//    }
  }

  @FXML
  private void clearFields() {
    locationArea.clear();
    sizeComboBox.setValue(null);
    statusComboBox.setValue(null);
  }
}
