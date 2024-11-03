package com.parkcontrol.persistence.entity;

public record ParkingSpot(
    int spotId,
    String section,   // Секція паркінгу (A, B, C, ...)
    int level,        // Поверх (1, 2, 3, ...)
    String spotNumber, // Номер місця в секції
    String status,     // Статус: "Вільне", "Зайняте", "Зарезервоване"
    String size        // Розмір: "Стандартне", "Велике", "Для інвалідів"
) implements Comparable<ParkingSpot> {

  @Override
  public int compareTo(ParkingSpot other) {
    // Спочатку сортуємо за секцією, потім за поверхом, і потім за номером місця
    int sectionComparison = this.section.compareTo(other.section);
    if (sectionComparison != 0) return sectionComparison;

    int levelComparison = Integer.compare(this.level, other.level);
    if (levelComparison != 0) return levelComparison;

    return this.spotNumber.compareTo(other.spotNumber);
  }
}
