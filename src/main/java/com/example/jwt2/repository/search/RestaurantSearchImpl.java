package com.example.jwt2.repository.search;

import com.example.jwt2.domain.QRestaurant;
import com.example.jwt2.domain.Restaurant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class RestaurantSearchImpl extends QuerydslRepositorySupport implements RestaurantSearch {
    public RestaurantSearchImpl() {
        super(Restaurant.class);
    }

    @Override
    public List<Restaurant> testSearch(Pageable pageable) {

        QRestaurant restaurant = QRestaurant.restaurant;
        JPQLQuery<Restaurant> query = from(restaurant);      // select ... from restaurant
        query.where(restaurant.location.contains("서"));     // where location like '%서%'

        this.getQuerydsl().applyPagination(pageable, query);

        List<Restaurant> restaurantList = query.fetch();
        long count = query.fetchCount();

        return null;
    }

    @Override
    public List<Restaurant> searchAll(String[] types, String keyword) {

        QRestaurant restaurant = QRestaurant.restaurant;
        JPQLQuery<Restaurant> query = from(restaurant);

        if((types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type : types) {
                switch (type) {
                    case "n":
                        booleanBuilder.or(restaurant.name.contains(keyword));
                        break;
                    case "l":
                        booleanBuilder.or(restaurant.location.contains(keyword));
                        break;
                    case "d":
                        booleanBuilder.or(restaurant.description.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        query.where(restaurant.id.gt(0L));

        List<Restaurant> list = query.fetch();
        long count = query.fetchCount();

        return list;
    }
}
