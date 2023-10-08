package com.example.jwt2.repository;

import com.example.jwt2.domain.Restaurant;
import com.example.jwt2.service.RestaurantService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Log4j2
public class RestaurantRepositoryTests {

    @Autowired
    private RestaurantService restaurantService;

    @Test
    @Transactional
    public void testViewAll() {
        List<Restaurant> restaurants = this.restaurantService.viewAll();
        log.info(restaurants.size());
    }
}
