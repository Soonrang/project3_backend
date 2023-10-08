package com.example.jwt2.service;

import com.example.jwt2.domain.Reservation;
import com.example.jwt2.dto.reservation.MyPageReservationResponseDTO;
import com.example.jwt2.dto.reservation.ReservationDTO;
import com.example.jwt2.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public void createReservation(ReservationDTO reservationDTO) {

        Reservation reservation = new Reservation();
        reservation.setApiUser(reservationDTO.getApiUser());
        reservation.setRestaurant(reservationDTO.getRestaurant());
        reservation.setNumberOfReservation(reservation.getNumberOfReservation());

        this.reservationRepository.save(reservation);
    }

    public void deleteReservation(Long reservationId) {
        this.reservationRepository.deleteById(reservationId);
    }
}
