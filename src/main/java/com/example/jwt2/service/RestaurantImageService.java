package com.example.jwt2.service;

import com.example.jwt2.domain.RestaurantImage;
import com.example.jwt2.repository.RestaurantImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantImageService {

    private final RestaurantImageRepository restaurantImageRepository;

    public RestaurantImage restaurantImageFindById(Long id) {
        return this.restaurantImageRepository.findById(id).orElseThrow();
    }
}
