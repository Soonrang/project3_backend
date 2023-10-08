package com.example.jwt2.service;

import com.example.jwt2.domain.Categories;
import com.example.jwt2.domain.Restaurant;
import com.example.jwt2.domain.RestaurantImage;
import com.example.jwt2.dto.restaurant.RestaurantDTO;
import com.example.jwt2.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // 식당 전체 출력 (대표 이미지 없음)
    public List<Restaurant> viewAll() {
        return this.restaurantRepository.findAll();
    }

    // 식당 전체 출력 + 대표 이미지
    public List<RestaurantDTO> viewAllWithImageOne() {
        List<Restaurant> restaurantList = this.restaurantRepository.findAll();
        List<RestaurantDTO> restaurantDTOList = new ArrayList<>();

        restaurantList.stream().forEach(restaurant -> {
            RestaurantDTO restaurantDTO = new RestaurantDTO();

            restaurantDTO.setId(restaurant.getId());
            restaurantDTO.setName(restaurant.getName());
            restaurantDTO.setLocation(restaurant.getLocation());
            restaurantDTO.setCategory(restaurant.getCategory());
            restaurantDTO.setDescription(restaurant.getDescription());
            restaurantDTO.setOpeningTime(restaurant.getOpeningTime());
            restaurantDTO.setClosingTime(restaurant.getClosingTime());
            // 이미지가 없는 경우를 대비하여 restaurant.getImages()의 null 체크와 비어있는지 확인
            if (restaurant.getImages() != null && !restaurant.getImages().isEmpty()) {
                restaurantDTO.setImageOneId(restaurant.getImages().get(0).getId());
            }
            restaurantDTO.setCallNumber(restaurant.getCallNumber());
            restaurantDTO.setReservationCount(restaurant.getReservationCount());

            restaurantDTOList.add(restaurantDTO);
        });

        return restaurantDTOList;
    }

    // id에 해당하는 식당 출력
    public Restaurant getRestaurant(Long id) {
        return this.restaurantRepository.findById(id).orElseThrow();
    }

    // 식당 등록 로직
    public Long createRestaurant(RestaurantDTO restaurantDTO) {

        Restaurant restaurant = new Restaurant();

        // 이미지 저장
        for(String imagePath : restaurantDTO.getImagePaths()) { // DTO에서 imagePaths를 직접 가져옵니다.
            RestaurantImage restaurantImage = new RestaurantImage();
            restaurantImage.setImagePath(imagePath);

            restaurantImage.setRestaurant(restaurant);
            restaurant.getImages().add(restaurantImage);
        }

        restaurant.setName(restaurantDTO.getName());
        restaurant.setLocation(restaurantDTO.getLocation());
        restaurant.setCategory(restaurantDTO.getCategory()); // 이미 Categories 타입이므로 변환 필요 없음
        restaurant.setDescription(restaurantDTO.getDescription());
        restaurant.setOpeningTime(restaurantDTO.getOpeningTime());
        restaurant.setClosingTime(restaurantDTO.getClosingTime());
        restaurant.setCallNumber(restaurantDTO.getCallNumber());
        restaurant.setImageCount(restaurantDTO.getImageCount()); // 저장된 이미지 개수

        return restaurantRepository.save(restaurant).getId();
    }

    // ★★★★★★★★★★★★★ 식당 예약순 top
    public List<Restaurant> topList() {
        Sort list = Sort.by(Sort.Direction.DESC, "reservationCount");
        return this.restaurantRepository.findAll(list);
    }
    public List<RestaurantDTO> viewAllWithImageOneSortedByReservationCount() {
        // 예약 횟수를 기준으로 내림차순 정렬된 Restaurant 리스트를 가져옵니다.
        Sort sort = Sort.by(Sort.Direction.DESC, "reservationCount");
        List<Restaurant> sortedRestaurantList = this.restaurantRepository.findAll(sort);

        // Restaurant 리스트를 RestaurantDTO 리스트로 변환합니다.
        List<RestaurantDTO> restaurantDTOList = new ArrayList<>();
        sortedRestaurantList.forEach(restaurant -> {
            RestaurantDTO restaurantDTO = new RestaurantDTO();

            restaurantDTO.setId(restaurant.getId());
            restaurantDTO.setName(restaurant.getName());
            restaurantDTO.setLocation(restaurant.getLocation());
            restaurantDTO.setCategory(restaurant.getCategory());
            restaurantDTO.setDescription(restaurant.getDescription());
            restaurantDTO.setOpeningTime(restaurant.getOpeningTime());
            restaurantDTO.setClosingTime(restaurant.getClosingTime());
            // 이미지가 없는 경우를 대비하여 restaurant.getImages()의 null 체크와 비어있는지 확인
            if (restaurant.getImages() != null && !restaurant.getImages().isEmpty()) {
                restaurantDTO.setImageOneId(restaurant.getImages().get(0).getId());
            }
            restaurantDTO.setCallNumber(restaurant.getCallNumber());
            restaurantDTO.setReservationCount(restaurant.getReservationCount());

            restaurantDTOList.add(restaurantDTO);
        });

        return restaurantDTOList;
    }


    // 식당 카테고리별 출력
    public List<Restaurant> getRestaurantsByCategory(Categories category) {
        return restaurantRepository.findAllByCategory(category);
    }
    public List<RestaurantDTO> viewAllWithImageOneByCategory(Categories category) {
        // 특정 카테고리에 속하는 Restaurant 리스트를 가져옵니다.
        List<Restaurant> restaurantList = this.restaurantRepository.findAllByCategory(category);

        // Restaurant 리스트를 RestaurantDTO 리스트로 변환합니다.
        List<RestaurantDTO> restaurantDTOList = new ArrayList<>();
        restaurantList.forEach(restaurant -> {
            RestaurantDTO restaurantDTO = new RestaurantDTO();

            restaurantDTO.setId(restaurant.getId());
            restaurantDTO.setName(restaurant.getName());
            restaurantDTO.setLocation(restaurant.getLocation());
            restaurantDTO.setCategory(restaurant.getCategory());
            restaurantDTO.setDescription(restaurant.getDescription());
            restaurantDTO.setOpeningTime(restaurant.getOpeningTime());
            restaurantDTO.setClosingTime(restaurant.getClosingTime());
            // 이미지가 없는 경우를 대비하여 restaurant.getImages()의 null 체크와 비어있는지 확인
            if (restaurant.getImages() != null && !restaurant.getImages().isEmpty()) {
                restaurantDTO.setImageOneId(restaurant.getImages().get(0).getId());
            }
            restaurantDTO.setCallNumber(restaurant.getCallNumber());
            restaurantDTO.setReservationCount(restaurant.getReservationCount());

            restaurantDTOList.add(restaurantDTO);
        });

        return restaurantDTOList;
    }

    public void plusReservationCount(Long id) {
        Restaurant restaurant = getRestaurant(id);
        restaurant.setReservationCount(restaurant.getReservationCount() + 1);
        this.restaurantRepository.save(restaurant);
    }

    // 검색을 활용한 리스트
    public List<RestaurantDTO> searchAll(String keyword) {
        // 타입은 일단 정해둠
        String[] types = {"n", "l"};
        List<Restaurant> restaurantList = this.restaurantRepository.searchAll(types, keyword);

        List<RestaurantDTO> restaurantDTOList = new ArrayList<>();
        restaurantList.forEach(restaurant -> {
            RestaurantDTO restaurantDTO = new RestaurantDTO();

            restaurantDTO.setId(restaurant.getId());
            restaurantDTO.setName(restaurant.getName());
            restaurantDTO.setLocation(restaurant.getLocation());
            restaurantDTO.setCategory(restaurant.getCategory());
            restaurantDTO.setDescription(restaurant.getDescription());
            restaurantDTO.setOpeningTime(restaurant.getOpeningTime());
            restaurantDTO.setClosingTime(restaurant.getClosingTime());
            if (restaurant.getImages() != null && !restaurant.getImages().isEmpty()) {
                restaurantDTO.setImageOneId(restaurant.getImages().get(0).getId());
            }
            restaurantDTO.setCallNumber(restaurant.getCallNumber());
            restaurantDTO.setReservationCount(restaurant.getReservationCount());

            restaurantDTOList.add(restaurantDTO);
        });

        return  restaurantDTOList;
    }

    public void removeOne(Long id) {
        this.restaurantRepository.deleteById(id);
    }
}





