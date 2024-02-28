package com.six.yummy.backoffice.service;

import com.six.yummy.backoffice.responsedto.RestaurantSalesResponse;
import com.six.yummy.backoffice.responsedto.TotalSalesResponse;
import com.six.yummy.global.exception.ValidateUserException;
import com.six.yummy.order.entity.Order;
import com.six.yummy.order.repository.OrderRepository;
import com.six.yummy.restaurant.entity.Restaurant;
import com.six.yummy.restaurant.repository.RestaurantRepository;
import com.six.yummy.user.entity.User;
import com.six.yummy.user.entity.UserRoleEnum;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BackOfficeService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public List<RestaurantSalesResponse> getSales(User user) {

        validateUser(user);

        List<Order> orders = orderRepository.findAll();
        Map<Restaurant, Integer> restaurantSales = new LinkedHashMap<>();

        for (Order order : orders) {
            restaurantSales.put(order.getRestaurant(),
                restaurantSales.getOrDefault(order.getRestaurant(), order.getTotalPrice())
                    + order.getTotalPrice());
        }
        List<Restaurant> restaurants = restaurantRepository.findAll();

        for (Restaurant restaurant : restaurants) {
            if (!restaurantSales.containsKey(restaurant)) {
                restaurantSales.put(restaurant, 0);
            }
        }

        List<RestaurantSalesResponse> restaurantSalesResponses = new ArrayList<>();
        for (Restaurant key : restaurantSales.keySet()) {
            restaurantSalesResponses.add(new RestaurantSalesResponse(key.getRestaurantName(),
                key.getContent(), key.getAddress(),
                key.getCategory(), restaurantSales.get(key)));
        }

        return restaurantSalesResponses;
    }

//    public List<TotalSalesResponse> getTotalSales(User user) {
//
//        validateUser(user);
//
//        LocalDateTime startDate = LocalDateTime.now().minusDays(15);
//        LocalDateTime endDate = LocalDateTime.now();
//
//        List<Order> orders = orderRepository.findByOrderedAtBetween(startDate, endDate);
//
//        Map<Integer, Integer> priceList = new LinkedHashMap<>();
//        for (Order order : orders) {
//            priceList.put(order.getOrderedAt().getDayOfYear()
//                , priceList.getOrDefault(order.getOrderedAt().getDayOfYear(), order.getTotalPrice()) + order.getTotalPrice());
//        }
//
//        int startDay = startDate.getDayOfYear();
//        int endDay = endDate.getDayOfYear();
//        for (int i = startDay; i < endDay; i++) {
//            if(!priceList.containsKey(i)){
//                priceList.put(i, 0);
//            }
//        }




//        List<Long> priceList = new ArrayList<>();
//
//        for (int i = 0; i < 15; i++) {
//            LocalDateTime start = LocalDateTime.now().minusDays(i);
//            LocalDateTime end = LocalDateTime.now();
//
//            Long price = 0L;
//
//            List<Order> orders1 = orderRepository.findByOrderedAtBetween(start, end);
//            for (Order order : orders1) {
//                price += order.getTotalPrice();
//            }
//            priceList.add(price);
//        }
//    }

    private void validateUser(User user){
        if(user.getRole() != UserRoleEnum.ADMIN){
            throw new ValidateUserException();
        }
    }
}