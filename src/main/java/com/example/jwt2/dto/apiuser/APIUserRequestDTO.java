package com.example.jwt2.dto.apiuser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class APIUserRequestDTO {
    private String mid;
    private String role;
    private String nickname;
    private String email;
    private LocalDate birth;
    private String phoneNumber;
}
