//package com.parkcontrol.presentation.controller;
//
//import com.parkcontrol.domain.exception.EntityNotFoundException;
//import com.parkcontrol.domain.security.AuthenticatedUser;
//import com.parkcontrol.persistence.connection.DatabaseConnection;
//import com.parkcontrol.persistence.entity.ParkingSpot;
//import com.parkcontrol.persistence.entity.Reservation;
//import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
//import com.parkcontrol.persistence.repository.impl.ReservationRepositoryImpl;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class MyReservationController {
//
//  @FXML
//  private Button cancelBookingButton;
//
//  @FXML
//  private Button editBookingButton;
//  @FXML
//  private TableColumn<Reservation, String> sectionColumn;
//  @FXML
//  private TableColumn<Reservation, String> levelColumn;
//  @FXML
//  private TableColumn<Reservation, String> spotNumberColumn;
//  @FXML
//  private TableColumn<Reservation, String> statusColumn;
//  @FXML
//  private TableColumn<Reservation, String> sizeColumn;
//  @FXML
//  private TableColumn<Reservation, LocalDateTime> startTimeColumn;
//  @FXML
//  private TableColumn<Reservation, LocalDateTime> endTimeColumn;
//  @FXML
//  private TableColumn<Reservation, Double> costColumn;
//
//
//  @FXML
//  private TextField searchTextField;
//
//
//
//
//  private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
//  private ParkingSpotRepositoryImpl parkingSpotRepository;
//  private ReservationRepositoryImpl reservationRepository;
//
//  public MyReservationController() {
//    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
//    this.reservationRepository = new ReservationRepositoryImpl(new DatabaseConnection().getDataSource());
//  }
//
//  @FXML
//  void initialize() {
//    sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().section()));
//    levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().level())));
//    spotNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().spotNumber()));
//    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
//    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));
//    startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().startTime().toString()));
//    endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().endTime().toString()));
//    costColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().cost()));
//
//    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterReservations(newValue));
//
//    loadMyReservations();
//
//    editBookingButton.setOnAction(event -> editReservation());
//    cancelBookingButton.setOnAction(event -> cancelReservation());
//  }
//
//  private void loadMyReservations() {
//    try {
//      int userId = AuthenticatedUser.getInstance().getCurrentUser().id();  // Отримуємо ID поточного користувача
//      List<Reservation> userReservations = reservationRepository.findByUserId(userId);
//      reservations.setAll(userReservations);
//      reservationTable.setItems(reservations);
//    } catch (Exception e) {
//      showAlert("Error", "Could not load reservations.", Alert.AlertType.ERROR);
//    }
//  }
//
//  private void editReservation() {
//    Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
//    if (selectedReservation != null) {
//      // Логіка редагування
//      // Наприклад, відкрий нове вікно для редагування
//      // Тут ти можеш реалізувати необхідні зміни і оновлення у базі даних
//      // reservationRepository.update(selectedReservation);
//      showAlert("Edit", "Editing reservation with ID: " + selectedReservation.reservationId(), Alert.AlertType.INFORMATION);
//    } else {
//      showAlert("Warning", "No reservation selected to edit.", Alert.AlertType.WARNING);
//    }
//  }
//
//  private void cancelReservation() {
//    Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
//    if (selectedReservation != null) {
//      Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this reservation?");
//      Optional<ButtonType> result = confirmationAlert.showAndWait();
//      if (result.isPresent() && result.get() == ButtonType.OK) {
//        try {
//          reservationRepository.deleteReservation(selectedReservation.reservationId());
//          reservations.remove(selectedReservation);
//        } catch (EntityNotFoundException e) {
//          showAlert("Error", "Reservation not found in the system.", Alert.AlertType.ERROR);
//        } catch (Exception e) {
//          showAlert("Error", "Could not cancel reservation.", Alert.AlertType.ERROR);
//        }
//      }
//    } else {
//      showAlert("Warning", "No reservation selected to cancel.", Alert.AlertType.WARNING);
//    }
//  }
//
//  private void filterReservations(String filter) {
//    if (filter == null || filter.isEmpty()) {
//      reservationTable.setItems(reservations);
//    } else {
//      String lowerCaseFilter = filter.toLowerCase();
//      ObservableList<Reservation> filteredReservations = reservations.stream()
//          .filter(reservation ->
//              reservation.startTime().toString().toLowerCase().contains(lowerCaseFilter) ||
//                  reservation.endTime().toString().toLowerCase().contains(lowerCaseFilter) ||
//                  String.valueOf(reservation.cost()).toLowerCase().contains(lowerCaseFilter))
//          .collect(Collectors.toCollection(FXCollections::observableArrayList));
//      reservationTable.setItems(filteredReservations);
//    }
//  }
//
//  private void showAlert(String title, String message, Alert.AlertType alertType) {
//    Alert alert = new Alert(alertType);
//    alert.setTitle(title);
//    alert.setHeaderText(null);
//    alert.setContentText(message);
//    alert.showAndWait();
//  }
//}
