package com.parkcontrol.presentation.controller;

import com.browniebytes.javafx.control.DateTimePicker;
import com.parkcontrol.domain.security.AuthenticatedUser;
import com.parkcontrol.domain.validation.ReservationValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.entity.Reservation;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import com.parkcontrol.persistence.repository.impl.ReservationRepositoryImpl;
import java.time.Duration;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class ReservationController {

  @FXML
  private Button bookingButton;

  @FXML
  private DateTimePicker endTimePicker;

  @FXML
  private TableColumn<ParkingSpot, String> levelColumn;

  @FXML
  private TextField levelField;

  @FXML
  private TableView<ParkingSpot> parkingSpotTable;

  @FXML
  private Label price;

  @FXML
  private TextField searchTextField;

  @FXML
  private TableColumn<ParkingSpot, String> sectionColumn;

  @FXML
  private TextField sectionField;

  @FXML
  private TableColumn<ParkingSpot, String> sizeColumn;

  @FXML
  private ComboBox<String> sizeComboBox;

  @FXML
  private TableColumn<ParkingSpot, String> spotNumberColumn;

  @FXML
  private TextField spotNumberField;

  @FXML
  private Label time;

  @FXML
  private DateTimePicker startTimePicker;

  @FXML
  private TableColumn<ParkingSpot, String> statusColumn;

  @FXML
  private ComboBox<String> statusComboBox;

  private List<ParkingSpot> parkingSpots;
  private ParkingSpotRepositoryImpl parkingSpotRepository;
  private ReservationRepositoryImpl reservationRepository;

  public ReservationController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
    this.reservationRepository = new ReservationRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().section()));
    levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().level())));
    spotNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().spotNumber()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));
    // Поля будуть нередагованими з самого початку
    sectionField.setEditable(false);
    levelField.setEditable(false);
    spotNumberField.setEditable(false);
    sizeComboBox.setDisable(true);
    statusComboBox.setDisable(true);

    loadParkingSpots();
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterParkingSpots(newValue));

    parkingSpotTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        fillFields(newValue);
      }
    });

    // Додано обробники подій на зміну дати та часу
    startTimePicker.dateTimeProperty().addListener((observable, oldValue, newValue) -> calculateCostAndShow());
    endTimePicker.dateTimeProperty().addListener((observable, oldValue, newValue) -> calculateCostAndShow());
    bookingButton.setOnAction(event -> handleBooking());
  }
  private void calculateCostAndShow() {
    LocalDateTime startDateTime = startTimePicker.dateTimeProperty().get();
    LocalDateTime endDateTime = endTimePicker.dateTimeProperty().get();

    if (startDateTime == null || endDateTime == null) {
      time.setText("—");
      price.setText("—");
      return;
    }

    Reservation reservation = new Reservation(0, 0, 0, startDateTime, endDateTime, 0);
    String validationMessage = ReservationValidator.validateReservationDates(reservation);

    if (validationMessage != null) {
      time.setText("Невірні дати");
      price.setText("—");
      return;
    }

    Duration duration = Duration.between(startDateTime, endDateTime);
    long hours = duration.toHours();
    long minutes = duration.toMinutes() % 60;

    String parkingTime = hours + " годин " + minutes + " хвилин";
    time.setText(parkingTime);

    double calculatedCost = calculateCost(hours);
    price.setText(calculatedCost + " грн.");
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
    AlertController.showAlert("Ваша броня успішно підтверджена!");
  }

  private double calculateCost(long hours) {
    return hours * 10;
  }

  private void handleBooking() {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    ParkingSpot selectedSpot = parkingSpotTable.getSelectionModel().getSelectedItem();

    if (selectedSpot == null) {
      AlertController.showAlert("Будь ласка, оберіть місце для паркування.");
      return;
    }

    // Перевірка, чи доступне місце для бронювання
    if (!"Вільне".equals(selectedSpot.status())) {
      AlertController.showAlert("Обране місце вже зайняте.");
      return;
    }

    LocalDateTime startDateTime = startTimePicker.dateTimeProperty().get();
    LocalDateTime endDateTime = endTimePicker.dateTimeProperty().get();
    double calculatedCost = calculateCost(Duration.between(startDateTime, endDateTime).toHours());

    Reservation tempReservation = new Reservation(0, currentUser.id(), selectedSpot.spotId(), startDateTime, endDateTime,calculatedCost);
    String validationMessage = ReservationValidator.validateReservationDates(tempReservation);
    if (validationMessage != null) {
      AlertController.showAlert(validationMessage);
      return;
    }

    Reservation reservation = new Reservation(
        0,
        currentUser.id(),
        selectedSpot.spotId(),
        startDateTime,
        endDateTime,
        calculatedCost
    );

    try {
      reservationRepository.addReservation(reservation);

      // Оновлення статусу місця на "Зайнятий"
      ParkingSpot updatedSpot = new ParkingSpot(
          selectedSpot.spotId(),
          selectedSpot.section(),
          selectedSpot.level(),
          selectedSpot.spotNumber(),
          "Зайняте", // новий статус
          selectedSpot.size()
      );

      parkingSpotRepository.updateParkingSpot(updatedSpot); // Метод для оновлення в базі даних

      showPaymentWindow();
      loadParkingSpots();
      clearFields(); // Очищення поля після бронювання
      parkingSpotTable.getSelectionModel().clearSelection();
    } catch (Exception e) {
      AlertController.showAlert("Не вдалося забронювати місце. Спробуйте ще раз.");
      e.printStackTrace();
    }
  }

  private void clearFields() {
    sectionField.clear();
    levelField.clear();
    spotNumberField.clear();
    sizeComboBox.setValue(null);
    statusComboBox.setValue(null);
    time.setText("");
    price.setText("");
  }
}
