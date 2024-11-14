package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.domain.security.AuthenticatedUser;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.entity.Reservation;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import com.parkcontrol.persistence.repository.impl.ReservationRepositoryImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MyReservationController {

  @FXML
  private Button cancelBookingButton;

  @FXML
  private TableView<Reservation> reservationTable;

  @FXML
  private TableColumn<Reservation, String> sectionColumn;
  @FXML
  private TableColumn<Reservation, String> levelColumn;
  @FXML
  private TableColumn<Reservation, String> spotNumberColumn;
  @FXML
  private TableColumn<Reservation, String> statusColumn;
  @FXML
  private TableColumn<Reservation, String> sizeColumn;
  @FXML
  private TableColumn<Reservation, LocalDateTime> startTimeColumn;
  @FXML
  private TableColumn<Reservation, LocalDateTime> endTimeColumn;
  @FXML
  private TableColumn<Reservation, Double> costColumn;

  @FXML
  private TextField searchTextField;

  private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
  private ParkingSpotRepositoryImpl parkingSpotRepository;
  private ReservationRepositoryImpl reservationRepository;

  public MyReservationController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
    this.reservationRepository = new ReservationRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    sectionColumn.setCellValueFactory(cellData -> {
      ParkingSpot spot = getParkingSpot(cellData.getValue().spotId());
      return new SimpleStringProperty(spot != null ? spot.section() : "N/A");
    });
    levelColumn.setCellValueFactory(cellData -> {
      ParkingSpot spot = getParkingSpot(cellData.getValue().spotId());
      return new SimpleStringProperty(spot != null ? String.valueOf(spot.level()) : "N/A");
    });
    spotNumberColumn.setCellValueFactory(cellData -> {
      ParkingSpot spot = getParkingSpot(cellData.getValue().spotId());
      return new SimpleStringProperty(spot != null ? spot.spotNumber() : "N/A");
    });
    statusColumn.setCellValueFactory(cellData -> {
      ParkingSpot spot = getParkingSpot(cellData.getValue().spotId());
      return new SimpleStringProperty(spot != null ? spot.status() : "N/A");
    });
    sizeColumn.setCellValueFactory(cellData -> {
      ParkingSpot spot = getParkingSpot(cellData.getValue().spotId());
      return new SimpleStringProperty(spot != null ? spot.size() : "N/A");
    });
    startTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().startTime()));
    endTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().endTime()));
    costColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().cost()));
    reservationTable.setOnMouseClicked(event -> {
      if (event.getClickCount() == 1) { // Перевірка на одиничне натискання
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem(); // Отримуємо вибране бронювання
        cancelBookingButton.setDisable(selectedReservation == null); // Якщо бронювання не вибрано, кнопка неактивна
      }
    });

    // Додати обробник події для кнопки скасування
    cancelBookingButton.setOnAction(event -> {
      Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
      if (selectedReservation != null) {
        cancelReservation(selectedReservation.reservationId());
      } else {
        AlertController.showAlert("Будь ласка, виберіть бронювання для скасування.");

      }
    });
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterReservations(newValue));

    loadMyReservations();
  }

  private ParkingSpot getParkingSpot(int spotId) {
    try {
      ParkingSpot spot = parkingSpotRepository.findById(spotId);
      return spot != null ? spot : null;
    } catch (Exception e) {
      AlertController.showAlert("Не вдалося отримати деталі місця паркування.");
      return null;
    }
  }

  private void loadMyReservations() {
    try {
      int userId = AuthenticatedUser.getInstance().getCurrentUser().id();  // Отримуємо ID поточного користувача
      List<Reservation> userReservations = reservationRepository.findByUserId(userId);
      reservations.setAll(userReservations);
      reservationTable.setItems(reservations);
    } catch (Exception e) {
      AlertController.showAlert("Не вдалося завантажити бронювання.");
    }
  }

  // Метод для скасування бронювання
  private void cancelReservation(int reservationId) {
    try {
      // Спочатку шукаємо бронювання за ID
      Reservation reservationToCancel = reservationRepository.findById(reservationId);

      // Знаходимо ID паркувального місця, яке було заброньовано
      int parkingSpotId = reservationToCancel.spotId();

      // Оновлюємо статус паркувального місця на "Вільне"
      updateParkingSpotStatus(parkingSpotId, "Вільне");

      // Тепер скасовуємо бронювання в базі даних
      reservationRepository.deleteReservation(reservationId);

      AlertController.showAlert("Бронювання успішно скасовано");

      // Видаляємо скасоване бронювання з таблиці
      reservationTable.getItems().removeIf(reservation -> reservation.reservationId() == reservationId);

    } catch (EntityNotFoundException e) {
      // Якщо бронювання не знайдено
      AlertController.showAlert("Не вдалося знайти бронювання");
    }
  }

  private void updateParkingSpotStatus(int parkingSpotId, String newStatus) throws EntityNotFoundException {
    // Шукаємо паркувальне місце за ID
    ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId);

    // Створюємо новий об'єкт паркувального місця з новим статусом
    ParkingSpot updatedSpot = new ParkingSpot(
        parkingSpot.spotId(),
        parkingSpot.section(),
        parkingSpot.level(),
        parkingSpot.spotNumber(),
        newStatus, // оновлений статус
        parkingSpot.size()
    );
    // Оновлюємо статус паркувального місця в базі даних
    parkingSpotRepository.updateParkingSpot(updatedSpot);
  }

  private void filterReservations(String filter) {
    if (filter == null || filter.isEmpty()) {
      reservationTable.setItems(reservations); // Показуємо всі резервації, якщо пошук порожній
    } else {
      String lowerCaseFilter = filter.toLowerCase();
      ObservableList<Reservation> filteredReservations = reservations.stream()
          .filter(reservation -> {
            ParkingSpot spot = getParkingSpot(reservation.spotId());

            // Фільтрація за полями Reservation та ParkingSpot
            return (reservation.startTime().toString().toLowerCase().contains(lowerCaseFilter) ||
                reservation.endTime().toString().toLowerCase().contains(lowerCaseFilter) ||
                String.valueOf(reservation.cost()).toLowerCase().contains(lowerCaseFilter) ||
                (spot != null && (spot.section().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(spot.level()).toLowerCase().contains(lowerCaseFilter) ||
                    spot.spotNumber().toLowerCase().contains(lowerCaseFilter) ||
                    spot.status().toLowerCase().contains(lowerCaseFilter) ||
                    spot.size().toLowerCase().contains(lowerCaseFilter))));
          })
          .collect(Collectors.toCollection(FXCollections::observableArrayList));

      reservationTable.setItems(filteredReservations);
      if (filteredReservations.isEmpty()) {
        reservationTable.setPlaceholder(new Label("На жаль, таких даних немає"));
      }
    }
  }
}
