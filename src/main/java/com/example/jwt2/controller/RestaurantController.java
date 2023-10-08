package com.example.jwt2.controller;

import com.example.jwt2.domain.Categories;
import com.example.jwt2.domain.Restaurant;
import com.example.jwt2.domain.RestaurantImage;
import com.example.jwt2.dto.restaurant.RestaurantDTO;
import com.example.jwt2.service.RestaurantImageService;
import com.example.jwt2.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantImageService restaurantImageService;

    // 식당 전체 출력
    /*
     * 명시적으로 ResponseEntity를 사용하여 응답을 반환하실 수 있습니다.
     * 이를 통해 HTTP 상태 코드와 헤더를 명시적으로 설정할 수 있으며, 이는 chunked encoding 문제를 해결할 수 있습니다.
     */
    @GetMapping("/list")
    public ResponseEntity<List<Restaurant>> getRestaurantAll() {
        List<Restaurant> restaurants = this.restaurantService.viewAll();

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // 모든 식당 리스트 + 대표 이미지 (테스트)
    @GetMapping("/listAllWithImageOne")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantAllWithImageOne() {
        List<RestaurantDTO> restaurants = this.restaurantService.viewAllWithImageOne();
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    /*
    * 컨트롤러에서 직접 변환
    * 컨트롤러에서 Restaurant 엔터티를 받아온 후, 직접 필요한 데이터만을 JSON으로 변환하는 방법
    * */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRestaurantOne(@PathVariable Long id) {
        Restaurant restaurant = this.restaurantService.getRestaurant(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", restaurant.getId());
        response.put("name", restaurant.getName());
        response.put("location", restaurant.getLocation());
        response.put("category", restaurant.getCategory());
        response.put("description", restaurant.getDescription());
        response.put("openingTime", restaurant.getOpeningTime());
        response.put("closingTime", restaurant.getClosingTime());
        response.put("callNumber", restaurant.getCallNumber());
        response.put("reservationCount", restaurant.getReservationCount());
        response.put("imageIds", restaurant.getImages().stream().map(RestaurantImage::getId).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{restaurantId}/image/{restaurantImageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long restaurantId, @PathVariable Long restaurantImageId) {
        RestaurantImage restaurantImage = restaurantImageService.restaurantImageFindById(restaurantImageId);

        if (restaurantImage == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String filePath = restaurantImage.getImagePath();
        try {
            Path path = Paths.get(filePath);
            byte[] imageBytes = Files.readAllBytes(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ★★★★★★★★★★★★★ 식당 예약횟수별 ~
    @GetMapping("/top")
    public ResponseEntity<List<Restaurant>> getTopRestaurants() {
        List<Restaurant> topRestaurants = this.restaurantService.topList();
        return new ResponseEntity<>(topRestaurants, HttpStatus.OK);
    }
    @GetMapping("/topWithImageOne")
    public ResponseEntity<List<RestaurantDTO>> getTopRestaurantsWithImageOne() {
        List<RestaurantDTO> topRestaurants = this.restaurantService.viewAllWithImageOneSortedByReservationCount();
        return new ResponseEntity<>(topRestaurants, HttpStatus.OK);
    }

    // 카테고리별
    @GetMapping("/byCategory/{category}")
    public List<Restaurant> getRestaurantsByCategory(@PathVariable Categories category) {
        return restaurantService.getRestaurantsByCategory(category);
    }
    @GetMapping("/byCategoryWithImageOne/{category}")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByCategoryWithImageOne(@PathVariable Categories category) {
        List<RestaurantDTO> filteredRestaurants = this.restaurantService.viewAllWithImageOneByCategory(category);
        return new ResponseEntity<>(filteredRestaurants, HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurant(@PathVariable String keyword) {
        List<RestaurantDTO> restaurantDTOList = this.restaurantService.searchAll(keyword);
        return new ResponseEntity<>(restaurantDTOList, HttpStatus.OK);
    }
}
