package com.parkcontrol.persistence.entity;

public record ParkingSpot(
    int spotId,
    String location,
    String status
) implements Comparable<ParkingSpot> {

  // Конструктор для перевірки статусу
  public ParkingSpot {
    if (!status.equals("FREE") && !status.equals("OCCUPIED")) {
      throw new IllegalArgumentException("Status must be 'FREE' or 'OCCUPIED'");
    }
  }

  @Override
  public int compareTo(ParkingSpot o) {
    return Integer.compare(this.spotId, o.spotId);
  }
}
