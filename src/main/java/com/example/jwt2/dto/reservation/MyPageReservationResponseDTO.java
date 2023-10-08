package com.example.jwt2.dto.reservation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyPageReservationResponseDTO {

    private String restaurant_name;

    private Long reservation_id;
}
