package com.example.jwt2.dto.apiuser;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateAPIUserRequestDTO {
    private String mid;
    private String mpw;
    private String nickname;
    private String email;
    private LocalDate birth;
    private String phoneNumber;
}
