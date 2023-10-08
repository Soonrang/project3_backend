package com.example.jwt2.dto.reservation;

import com.example.jwt2.domain.APIUser;
import com.example.jwt2.domain.Restaurant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReservationDTO {
    private APIUser apiUser;
    private Restaurant restaurant;
    private Integer numberOfReservation;
}
