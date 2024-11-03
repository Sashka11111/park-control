package com.parkcontrol.persistence.entity;

import java.time.LocalDateTime;
import java.util.Date;

public record Reservation(
    int reservationId,
    int userId,
    int spotId,
    LocalDateTime startTime,
    LocalDateTime endTime
) implements Comparable<Reservation> {
  // Додатковий конструктор для валідації
  public Reservation(LocalDateTime startTime, LocalDateTime endTime) {
    this(0, 0, 0, startTime, endTime); // Встановлюємо значення за замовчуванням
  }
  @Override
  public int compareTo(Reservation other) {
    return Integer.compare(this.reservationId, other.reservationId);
  }
}
