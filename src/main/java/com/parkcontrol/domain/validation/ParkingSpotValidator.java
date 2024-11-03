package com.parkcontrol.domain.validation;

import com.parkcontrol.persistence.entity.ParkingSpot;
import java.util.List;
import java.util.Optional;

public class ParkingSpotValidator {

  private static final String SECTION_REGEX = "[A-Z]";
  private static final String[] VALID_STATUSES = {"Вільне", "Зайняте", "Зарезервоване"};
  private static final String[] VALID_SIZES = {"Стандартне", "Велике", "Для інвалідів"};

  public static String validateParkingSpot(ParkingSpot parkingSpot, List<ParkingSpot> existingSpots) {
    return validateSection(parkingSpot.section())
        .or(() -> validateLevel(parkingSpot.level()))
        .or(() -> validateSpotNumber(parkingSpot.spotNumber()))
        .or(() -> validateStatus(parkingSpot.status()))
        .or(() -> validateSize(parkingSpot.size()))
        .or(() -> validateUniqueSpot(parkingSpot, existingSpots))
        .orElse(null); // Усі поля валідні
  }

  private static Optional<String> validateSection(String section) {
    if (section == null || section.trim().isEmpty() || !section.matches(SECTION_REGEX)) {
      return Optional.of("Секція повинна містити одну букву (A, B, C...).");
    }
    return Optional.empty(); // Секція валідна
  }

  private static Optional<String> validateLevel(int level) {
    if (level < 1) {
      return Optional.of("Поверх повинен бути більше або рівний 1.");
    }
    return Optional.empty(); // Поверх валідний
  }

  private static Optional<String> validateSpotNumber(String spotNumber) {
    if (spotNumber == null || spotNumber.trim().isEmpty()) {
      return Optional.of("Номер місця не може бути порожнім.");
    }
    return Optional.empty(); // Номер місця валідний
  }

  private static Optional<String> validateStatus(String status) {
    for (String validStatus : VALID_STATUSES) {
      if (validStatus.equals(status)) {
        return Optional.empty(); // Статус валідний
      }
    }
    return Optional.of("Паркувальне місце може бути 'Вільне', 'Зайняте' або 'Зарезервоване'.");
  }

  private static Optional<String> validateSize(String size) {
    for (String validSize : VALID_SIZES) {
      if (validSize.equals(size)) {
        return Optional.empty(); // Розмір валідний
      }
    }
    return Optional.of("Паркувальне місце може бути 'Стандартне', 'Велике' або 'Для інвалідів'.");
  }

  private static Optional<String> validateUniqueSpot(ParkingSpot parkingSpot, List<ParkingSpot> existingSpots) {
    boolean exists = existingSpots.stream().anyMatch(spot ->
        spot.spotId() != parkingSpot.spotId() && // Перевіряємо, чи не те саме місце
            spot.section().equals(parkingSpot.section()) &&
            spot.level() == parkingSpot.level() &&
            spot.spotNumber().equals(parkingSpot.spotNumber())
    );
    return exists ? Optional.of("Паркувальне місце з такими даними вже існує.") : Optional.empty();
  }

}
