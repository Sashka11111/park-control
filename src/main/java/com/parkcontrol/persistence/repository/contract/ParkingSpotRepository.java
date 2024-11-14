package com.parkcontrol.persistence.repository.contract;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.ParkingSpot;
import java.util.List;

public interface ParkingSpotRepository {
  List<ParkingSpot> findAll(); // Знайти всі паркувальні місця

  int addParkingSpot(ParkingSpot parkingSpot); // Додати нове паркувальне місце

  ParkingSpot findById(int spotId) throws EntityNotFoundException; // Знайти паркувальне місце за ID

  void updateParkingSpot(ParkingSpot parkingSpot) throws EntityNotFoundException; // Оновити інформацію про паркувальне місце

  void deleteParkingSpot(int spotId) throws EntityNotFoundException; // Видалити паркувальне місце за ID

  // Методи для зв'язку багато до багатьох
  void addCategoryToParkingSpot(int spotId, int categoryId); // Додати категорію до паркувального місця

  void removeCategoryFromParkingSpot(int spotId, int categoryId) throws EntityNotFoundException; // Видалити категорію з паркувального місця

  List<Integer> getCategoriesByParkingSpotId(int spotId); // Отримати категорії для паркувального місця
}
