package com.example.jwt2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Categories category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    /*
    * JPA에서 양방향 연관관계를 설정할 때, 한쪽은 mappedBy 속성을 사용합니다.
    * 이때, mappedBy를 사용하는 쪽에 @JsonIgnore를 추가하면 JSON 변환 시 무한 루프에 빠지는 문제를 방지할 수 있습니다.
    * net::ERR_INCOMPLETE_CHUNKED_ENCODING 오류 해결을 위한 한가지 방안
    * */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RestaurantImage> images = new ArrayList<>();

    @Column(nullable = false)
    private String callNumber;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> reservationList = new ArrayList<>();

    private Integer reservationCount;

    private Integer imageCount;

    @PrePersist
    public void initialize() {
        if (reservationCount == null) {
            reservationCount = 0;
        }
        if (imageCount == null) {
            imageCount = 0;
        }
    }
}
