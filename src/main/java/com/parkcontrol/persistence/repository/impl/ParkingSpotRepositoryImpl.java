package com.parkcontrol.persistence.repository.impl;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.repository.contract.ParkingSpotRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class ParkingSpotRepositoryImpl implements ParkingSpotRepository {

  private final DataSource dataSource;

  public ParkingSpotRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<ParkingSpot> findAll() {
    List<ParkingSpot> parkingSpots = new ArrayList<>();
    String query = "SELECT * FROM ParkingSpots";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        ParkingSpot parkingSpot = mapParkingSpot(resultSet);
        parkingSpots.add(parkingSpot);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return parkingSpots;
  }

  @Override
  public void addParkingSpot(ParkingSpot parkingSpot) {
    String query = "INSERT INTO ParkingSpots (location, status, size) VALUES (?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, parkingSpot.location());
      preparedStatement.setString(2, parkingSpot.status());
      preparedStatement.setString(3, parkingSpot.size()); // Додаємо поле size
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public ParkingSpot findById(int spotId) throws EntityNotFoundException {
    String query = "SELECT * FROM ParkingSpots WHERE spot_id = ?";
    ParkingSpot parkingSpot = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, spotId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          parkingSpot = mapParkingSpot(resultSet);
        } else {
          throw new EntityNotFoundException("Паркувальне місце з ID " + spotId + " не знайдено");
        }
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Помилка під час отримання паркувального місця з ID: " + spotId, e);
    }
    return parkingSpot;
  }

  @Override
  public void updateParkingSpot(ParkingSpot parkingSpot) throws EntityNotFoundException {
    String query = "UPDATE ParkingSpots SET location = ?, status = ?, size = ? WHERE spot_id = ?"; // Додаємо size
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, parkingSpot.location());
      preparedStatement.setString(2, parkingSpot.status());
      preparedStatement.setString(3, parkingSpot.size()); // Додаємо поле size
      preparedStatement.setInt(4, parkingSpot.spotId());
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Паркувальне місце з ID " + parkingSpot.spotId() + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deleteParkingSpot(int spotId) throws EntityNotFoundException {
    String query = "DELETE FROM ParkingSpots WHERE spot_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, spotId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Паркувальне місце з ID " + spotId + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<ParkingSpot> findByStatus(String status) {
    List<ParkingSpot> parkingSpots = new ArrayList<>();
    String query = "SELECT * FROM ParkingSpots WHERE status = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, status);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          ParkingSpot parkingSpot = mapParkingSpot(resultSet);
          parkingSpots.add(parkingSpot);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return parkingSpots;
  }

  private ParkingSpot mapParkingSpot(ResultSet resultSet) throws SQLException {
    int spotId = resultSet.getInt("spot_id");
    String location = resultSet.getString("location");
    String status = resultSet.getString("status");
    String size = resultSet.getString("size"); // Додаємо поле size
    return new ParkingSpot(spotId, location, status, size); // Включаємо size в конструктор
  }
}
