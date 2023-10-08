package com.example.jwt2.dto.restaurant;

import com.example.jwt2.domain.Categories;
import com.example.jwt2.domain.RestaurantImage;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDTO {
    private Long id;
    private String name;
    private String location;
    private Categories category;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private List<String> imagePaths; // 파일 경로를 나타내는 문자열 목록으로 변경, 이미지 여러개
    private Long imageOneId; // 이미지 하나
    private String callNumber;
    private Integer reservationCount;
    private Integer imageCount;
}
