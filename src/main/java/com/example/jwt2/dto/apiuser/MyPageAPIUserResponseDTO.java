package com.example.jwt2.dto.apiuser;

import com.example.jwt2.domain.Reservation;
import com.example.jwt2.dto.reservation.MyPageReservationResponseDTO;
import com.example.jwt2.dto.reservation.ReservationDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class MyPageAPIUserResponseDTO {

    private String mid;
    private String mpw;
    private String nickname;
    private String email;
    private LocalDate birth;
    private String phoneNumber;
    private List<MyPageReservationResponseDTO> reservationList;

}
