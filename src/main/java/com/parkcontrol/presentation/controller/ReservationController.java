package com.parkcontrol.presentation.controller;

import com.browniebytes.javafx.control.DateTimePicker;
import com.parkcontrol.domain.validation.ReservationValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.entity.Reservation;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Date;
import java.util.List;
import java.time.Duration;

public class ReservationController {

  @FXML
  private Button bookingButton;

  @FXML
  private Label endTimeLabel;

  @FXML
  private DateTimePicker endTimePicker;

  @FXML
  private TableColumn<ParkingSpot, String> levelColumn;

  @FXML
  private TextField levelField;

  @FXML
  private Label levelLabel;

  @FXML
  private TableView<ParkingSpot> parkingSpotTable;

  @FXML
  private Label price;

  @FXML
  private Label priceLabel;

  @FXML
  private TextField searchTextField;

  @FXML
  private TableColumn<ParkingSpot, String> sectionColumn;

  @FXML
  private TextField sectionField;

  @FXML
  private Label sectionLabel;

  @FXML
  private TableColumn<ParkingSpot, String> sizeColumn;

  @FXML
  private ComboBox<String> sizeComboBox;

  @FXML
  private Label sizeLabel;

  @FXML
  private TableColumn<ParkingSpot, String> spotNumberColumn;

  @FXML
  private TextField spotNumberField;

  @FXML
  private Label spotNumberLabel;

  @FXML
  private Label time;

  @FXML
  private Label startTimeLabel;

  @FXML
  private DateTimePicker startTimePicker;

  @FXML
  private TableColumn<ParkingSpot, String> statusColumn;

  @FXML
  private ComboBox<String> statusComboBox;

  @FXML
  private Label statusLabel;

  private List<ParkingSpot> parkingSpots;
  private ParkingSpotRepositoryImpl parkingSpotRepository;

  public ReservationController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().section()));
    levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().level())));
    spotNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().spotNumber()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));

    loadParkingSpots();
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterParkingSpots(newValue));

    parkingSpotTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        fillFields(newValue);
      }
    });

    // Додати обробники подій на зміну дати та часу
    startTimePicker.dateTimeProperty().addListener((observable, oldValue, newValue) -> calculateCostAndShow());
    endTimePicker.dateTimeProperty().addListener((observable, oldValue, newValue) -> calculateCostAndShow());
  }

  private void calculateCostAndShow() {
    Date startDate = startTimePicker.getTime();
    Date endDate = endTimePicker.getTime();

    // Перевірка на null для обох дат
    if (startDate == null || endDate == null) {
      time.setText("—");
      price.setText("—");
      return;
    }

    // Конвертуємо Date в LocalDateTime
    LocalDateTime startDateTime = startDate.toInstant()
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDateTime();
    LocalDateTime endDateTime = endDate.toInstant()
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDateTime();

    Reservation reservation = new Reservation(startDateTime, endDateTime);
    String validationMessage = ReservationValidator.validateReservationDates(reservation);

    if (validationMessage != null) {
      time.setText("Невірні дати");
      price.setText("—");
      return;
    }

    // Обчислення часу паркування
    Duration duration = Duration.between(startDateTime, endDateTime);
    long hours = duration.toHours();
    long minutes = duration.toMinutes() % 60; // Залишок хвилин після повних годин

    // Відображення загального часу паркування
    String parkingTime = hours + " годин " + minutes + " хвилин";
    time.setText(parkingTime);

    // Обчислення та відображення ціни
    double cost = calculateCost(hours);
    price.setText(cost + " грн.");
  }


  private void loadParkingSpots() {
    parkingSpots = parkingSpotRepository.findAll();
    parkingSpotTable.setItems(FXCollections.observableArrayList(parkingSpots));
  }

  private void fillFields(ParkingSpot spot) {
    sectionField.setText(spot.section());
    levelField.setText(String.valueOf(spot.level()));
    spotNumberField.setText(spot.spotNumber());
    sizeComboBox.setValue(spot.size());
    statusComboBox.setValue(spot.status());

    // Деактивувати поля для редагування
    sectionField.setEditable(false);
    levelField.setEditable(false);
    spotNumberField.setEditable(false);
  }

  private void filterParkingSpots(String searchText) {
    if (searchText.isEmpty()) {
      parkingSpotTable.setItems(FXCollections.observableArrayList(parkingSpots));
      return;
    }

    List<ParkingSpot> filteredList = parkingSpots.stream()
        .filter(spot -> {
          String section = spot.section().toLowerCase();
          String spotNumber = spot.spotNumber().toLowerCase();
          String status = spot.status().toLowerCase();
          String size = spot.size().toLowerCase();
          int level = spot.level();

          return section.contains(searchText.toLowerCase()) ||
              spotNumber.contains(searchText.toLowerCase()) ||
              status.contains(searchText.toLowerCase()) ||
              size.contains(searchText.toLowerCase()) ||
              String.valueOf(level).contains(searchText);
        })
        .toList();
    parkingSpotTable.setItems(FXCollections.observableArrayList(filteredList));

    if (filteredList.isEmpty()) {
      parkingSpotTable.setPlaceholder(new Label("На жаль, таких даних немає"));
    }
  }
  private void showPaymentWindow() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ваша броня підтверджена! Загальна вартість: " + price.getText());
    alert.setTitle("Оплата");
    alert.setHeaderText("Підтвердження оплати");
    alert.showAndWait();
  }

  private double calculateCost(long hours) {
    // Припустимо, що ціна паркування становить 10 грн. за годину
    return hours * 10;
  }
}
