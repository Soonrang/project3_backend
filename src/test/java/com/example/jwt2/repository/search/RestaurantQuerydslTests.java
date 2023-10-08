package com.example.jwt2.repository.search;

import com.example.jwt2.domain.Restaurant;
import com.example.jwt2.repository.RestaurantRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@SpringBootTest
@Log4j2
public class RestaurantQuerydslTests {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Test
    public void testSearchTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("imageCount").ascending());
        this.restaurantRepository.testSearch(pageable);
    }

    @Test
    public void testSearchAll() {
        String[] types = {"n", "l", "d"};

        List<Restaurant> result = this.restaurantRepository.searchAll(types, "다");

        result.stream().forEach(restaurant -> {
            log.info(restaurant.getName());
        });
    }
}
