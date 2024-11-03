package com.parkcontrol.domain.validation;

import com.parkcontrol.persistence.entity.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReservationValidator {

  public static String validateReservationDates(Reservation reservation) {
    return validateStartTime(reservation.startTime(), reservation.endTime())
        .or(() -> validateEndTime(reservation.endTime(), reservation.startTime()))
        .orElse(null); // Усі дати валідні
  }

  private static Optional<String> validateStartTime(LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime == null) {
      return Optional.of("Час початку не може бути порожнім.");
    }
    return Optional.empty(); // Час початку валідний
  }

  private static Optional<String> validateEndTime(LocalDateTime endTime, LocalDateTime startTime) {
    if (endTime == null || endTime.isBefore(startTime)) {
      return Optional.of("Час закінчення не може бути порожнім і повинен бути після часу початку.");
    }
    return Optional.empty(); // Час закінчення валідний
  }
}
