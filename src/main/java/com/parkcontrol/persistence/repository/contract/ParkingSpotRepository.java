package com.parkcontrol.persistence.repository.contract;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.ParkingSpot;
import java.util.List;

public interface ParkingSpotRepository {
  List<ParkingSpot> findAll(); // Знайти всі паркувальні місця
  void addParkingSpot(ParkingSpot parkingSpot); // Додати нове паркувальне місце
  ParkingSpot findById(int spotId) throws EntityNotFoundException; // Знайти паркувальне місце за ID
  void updateParkingSpot(ParkingSpot parkingSpot) throws EntityNotFoundException; // Оновити інформацію про паркувальне місце
 void deleteParkingSpot(int spotId) throws EntityNotFoundException; // Видалити паркувальне місце за ID
}
