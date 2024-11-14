package com.parkcontrol.persistence.repository.contract;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.Reservation;
import java.util.List;

public interface ReservationRepository {

  void addReservation(Reservation reservation); // Додати нове бронювання

  Reservation findById(int reservationId) throws EntityNotFoundException; // Знайти бронювання за ID

  void deleteReservation(int reservationId) throws EntityNotFoundException; // Видалити бронювання за ID

  List<Reservation> findByUserId(int userId); // Знайти бронювання за ID користувача
}
