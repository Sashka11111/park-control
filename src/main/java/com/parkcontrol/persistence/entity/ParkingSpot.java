package com.parkcontrol.persistence.entity;

public record ParkingSpot(
    int spotId,
    String location,
    String status,
    String size
) implements Comparable<ParkingSpot> {

  @Override
  public int compareTo(ParkingSpot o) {
    return Integer.compare(this.spotId, o.spotId);
  }
}
