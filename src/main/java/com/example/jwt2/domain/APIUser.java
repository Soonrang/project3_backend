package com.example.jwt2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class APIUser {

    @Id
    @Column(name="member_id")
    private String mid;

    @Column(name="member_password", nullable = false)
    private String mpw;

    @Column(nullable = false)
    @Builder.Default
    private String role = "ROLE_USER";  // 기본 권한은 일반 사용자로 설정

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "apiUser", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Reservation> reservationList = new ArrayList<>();

    public void changePw(String mpw) {
        this.mpw = mpw;
    }
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    public void changeEmail(String email) { this.email = email; }
    public void changePhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
