package com.parkcontrol.domain.validation;

import com.parkcontrol.persistence.entity.Reservation;
import java.time.LocalDateTime;
import java.util.Optional;

public class ReservationValidator {
  /**
   * Перевірка на валідність дат початку і закінчення.
   * @param reservation об'єкт бронювання, який містить дати
   * @return повідомлення про помилку, якщо дати невалідні, або null, якщо все валідно
   */
  public static String validateReservationDates(Reservation reservation) {
    return validateStartTime(reservation.startTime())
        .or(() -> validateEndTime(reservation.endTime(), reservation.startTime()))
        .orElse(null); // Усі дати валідні
  }

  /**
   * Перевірка валідності дати початку.
   * @param startTime час початку бронювання
   * @return Optional з повідомленням про помилку, якщо перевірка не пройшла, або порожній Optional, якщо все валідно
   */
  private static Optional<String> validateStartTime(LocalDateTime startTime) {
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
