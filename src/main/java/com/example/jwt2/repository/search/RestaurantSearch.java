package com.example.jwt2.repository.search;

import com.example.jwt2.domain.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RestaurantSearch {
    List<Restaurant> testSearch(Pageable pageable);

    List<Restaurant> searchAll(String[] types, String keyword);
}
