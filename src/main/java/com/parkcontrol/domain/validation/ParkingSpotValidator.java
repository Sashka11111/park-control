package com.parkcontrol.domain.validation;

import com.parkcontrol.persistence.entity.ParkingSpot;
import java.util.List;

public class ParkingSpotValidator {

  /**
   * Метод для валідації об'єкта ParkingSpot.
   *
   * @param parkingSpot об'єкт ParkingSpot для валідації
   * @param existingSpots список існуючих паркувальних місць для перевірки унікальності
   * @return null, якщо всі поля об'єкта валідні; повідомлення про помилку, якщо валідація не пройшла
   */
  public static String validateParkingSpot(ParkingSpot parkingSpot, List<ParkingSpot> existingSpots) {
    if (!validateLocation(parkingSpot.location(), existingSpots)) {
      return "Локація не повинна повторювати та містити порожні значення.";
    }
    if (!validateStatus(parkingSpot.status())) {
      return "Паркувальне місце може бути 'Вільне', або 'Зайняте'.";
    }
    if (!validateSize(parkingSpot.size())) {
      return "Паркувальне місце може бути 'Стандартне', 'Велике', або 'Для інвалідів'.";
    }
    return null; // Усі поля валідні
  }

  private static boolean validateLocation(String location, List<ParkingSpot> existingSpots) {
    if (location == null || location.trim().isEmpty()) {
      return false;
    }
    return existingSpots.stream().noneMatch(spot -> spot.location().equals(location));
  }

  private static boolean validateStatus(String status) {
    return "Вільне".equals(status) || "Зайняте".equals(status);
  }

  private static boolean validateSize(String size) {
    return "Стандартне".equals(size) || "Велике".equals(size) || "Для інвалідів".equals(size);
  }
}
