package com.example.jwt2.repository;

import com.example.jwt2.domain.Categories;
import com.example.jwt2.domain.Restaurant;
import com.example.jwt2.repository.search.RestaurantSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantSearch {
    List<Restaurant> findAllByCategory(Categories category);
}
