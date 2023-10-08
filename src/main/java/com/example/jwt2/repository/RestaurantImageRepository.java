package com.example.jwt2.repository;

import com.example.jwt2.domain.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, Long> {

}
