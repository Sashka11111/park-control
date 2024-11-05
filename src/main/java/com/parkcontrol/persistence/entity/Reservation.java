package com.parkcontrol.persistence.entity;

import java.time.LocalDateTime;

public record Reservation(
    int reservationId,
    int userId,
    int spotId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    double cost
) implements Comparable<Reservation> {

  @Override
  public int compareTo(Reservation other) {
    return Integer.compare(this.reservationId, other.reservationId);
  }
}
