package com.parkcontrol.persistence.repository.impl;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.Reservation;
import com.parkcontrol.persistence.repository.contract.ReservationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class ReservationRepositoryImpl implements ReservationRepository {

  private final DataSource dataSource;

  public ReservationRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void addReservation(Reservation reservation) {
    String query = "INSERT INTO Reservations (user_id, spot_id, start_time, end_time, cost) VALUES (?, ?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, reservation.userId());
      preparedStatement.setInt(2, reservation.spotId());
      preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(reservation.startTime()));
      preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(reservation.endTime()));
      preparedStatement.setDouble(5, reservation.cost());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Reservation findById(int reservationId) throws EntityNotFoundException {
    String query = "SELECT * FROM Reservations WHERE reservation_id = ?";
    Reservation reservation = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, reservationId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          reservation = mapReservation(resultSet);
        } else {
          throw new EntityNotFoundException("Бронювання з ID " + reservationId + " не знайдено");
        }
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Помилка під час отримання бронювання з ID: " + reservationId, e);
    }
    return reservation;
  }

  @Override
  public void deleteReservation(int reservationId) throws EntityNotFoundException {
    String query = "DELETE FROM Reservations WHERE reservation_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, reservationId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Бронювання з ID " + reservationId + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Reservation> findByUserId(int userId) {
    List<Reservation> reservations = new ArrayList<>();
    String query = "SELECT * FROM Reservations WHERE user_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, userId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          Reservation reservation = mapReservation(resultSet);
          reservations.add(reservation);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reservations;
  }

  private Reservation mapReservation(ResultSet resultSet) throws SQLException {
    int reservationId = resultSet.getInt("reservation_id");
    int userId = resultSet.getInt("user_id");
    int spotId = resultSet.getInt("spot_id");
    java.sql.Timestamp startTimeStamp = resultSet.getTimestamp("start_time");
    java.sql.Timestamp endTimeStamp = resultSet.getTimestamp("end_time");
    double cost = resultSet.getDouble("cost");

    // Конвертація Timestamp у LocalDateTime
    java.time.LocalDateTime startTime = startTimeStamp.toLocalDateTime();
    java.time.LocalDateTime endTime = endTimeStamp.toLocalDateTime();

    return new Reservation(reservationId, userId, spotId, startTime, endTime, cost);
  }
}
