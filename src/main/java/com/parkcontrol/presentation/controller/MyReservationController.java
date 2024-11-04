package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.domain.security.AuthenticatedUser;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.entity.Reservation;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import com.parkcontrol.persistence.repository.impl.ReservationRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyReservationController {

  @FXML
  private Button cancelBookingButton;

  @FXML
  private Button editBookingButton;

  @FXML
  private TableColumn<Reservation, String> endTimeColumn;

  @FXML
  private TableColumn<ParkingSpot, String> levelColumn;

  @FXML
  private TableView<Reservation> reservationTable;

  @FXML
  private TextField searchTextField;

  @FXML
  private TableColumn<ParkingSpot, String> sectionColumn;

  @FXML
  private TableColumn<ParkingSpot, String> sizeColumn;

  @FXML
  private TableColumn<ParkingSpot, String> spotNumberColumn;

  @FXML
  private TableColumn<Reservation, String> startTimeColumn;

  @FXML
  private TableColumn<ParkingSpot, String> statusColumn;

  private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
  private ParkingSpotRepositoryImpl parkingSpotRepository;
  private ReservationRepositoryImpl reservationRepository;

  public MyReservationController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
    this.reservationRepository = new ReservationRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().section()));
    levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().level())));
    spotNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().spotNumber()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));
    startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().startTime().toString()));
    endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().endTime().toString()));

    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterReservations(newValue));

    loadMyReservations();

    editBookingButton.setOnAction(event -> editReservation());
    cancelBookingButton.setOnAction(event -> cancelReservation());
  }

  private void loadMyReservations() {

  }

  private void editReservation() {
  }

  private void cancelReservation() {
  }

  private void filterReservations(String filter) {
 }

}
