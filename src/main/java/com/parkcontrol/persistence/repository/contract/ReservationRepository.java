package com.parkcontrol.persistence.repository.contract;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.Reservation;
import java.util.List;
import java.util.Map;

public interface ReservationRepository {

  List<Reservation> findAll(); // Знайти всі бронювання

  void addReservation(Reservation reservation); // Додати нове бронювання

  Reservation findById(int reservationId) throws EntityNotFoundException; // Знайти бронювання за ID

  void updateReservation(Reservation reservation) throws EntityNotFoundException; // Оновити інформацію про бронювання

  void deleteReservation(int reservationId) throws EntityNotFoundException; // Видалити бронювання за ID

  List<Reservation> findByUserId(int userId); // Знайти бронювання за ID користувача

  List<Map<String, Object>> findAllReservationsWithParkingSpots(); // Знайти всі бронювання з даними паркувальних місць
}
