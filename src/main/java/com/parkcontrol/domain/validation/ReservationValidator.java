package com.parkcontrol.domain.validation;

import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.entity.Reservation;
import java.time.LocalDateTime;
import java.util.Optional;

public class ReservationValidator {

  /**
   * Перевірка валідності дат початку і закінчення, а також доступності місця.
   * @param reservation об'єкт бронювання, який містить дати
   * @param spot об'єкт паркувального місця, який перевіряється на доступність
   * @return повідомлення про помилку, якщо перевірка не пройшла, або null, якщо все валідно
   */
  public static String validateReservationDatesAndSpot(Reservation reservation, ParkingSpot spot) {
    // Перевірка валідності дат
    String dateValidation = validateReservationDates(reservation);
    if (dateValidation != null) {
      return dateValidation;
    }

    // Перевірка, чи місце не зайняте або вже заброньоване
    if ("Зайняте".equals(spot.status()) || "Зарезервоване".equals(spot.status())) {
      return "Обране місце вже зайняте або заброньоване.";
    }

    return null; // Місце і дати валідні
  }

  /**
   * Перевірка на валідність дат початку і закінчення.
   * @param reservation об'єкт бронювання, який містить дати
   * @return повідомлення про помилку, якщо дати невалідні, або null, якщо все валідно
   */
  public static String validateReservationDates(Reservation reservation) {
    return validateStartTime(reservation.startTime(), reservation.endTime())
        .or(() -> validateEndTime(reservation.endTime(), reservation.startTime()))
        .orElse(null); // Усі дати валідні
  }

  /**
   * Перевірка валідності дати початку.
   * @param startTime час початку бронювання
   * @param endTime час закінчення бронювання
   * @return Optional з повідомленням про помилку, якщо перевірка не пройшла, або порожній Optional, якщо все валідно
   */
  private static Optional<String> validateStartTime(LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime == null) {
      return Optional.of("Час початку не може бути порожнім.");
    }
    return Optional.empty(); // Час початку валідний
  }

  /**
   * Перевірка валідності дати закінчення.
   * @param endTime час закінчення бронювання
   * @param startTime час початку бронювання
   * @return Optional з повідомленням про помилку, якщо перевірка не пройшла, або порожній Optional, якщо все валідно
   */
  private static Optional<String> validateEndTime(LocalDateTime endTime, LocalDateTime startTime) {
    if (endTime == null || endTime.isBefore(startTime)) {
      return Optional.of("Час закінчення не може бути порожнім і повинен бути після часу початку.");
    }
    return Optional.empty(); // Час закінчення валідний
  }
}
