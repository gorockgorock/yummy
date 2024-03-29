package com.six.yummy.review.service;


import com.six.yummy.global.exception.DataIntegrityViolationLikeException;
import com.six.yummy.global.exception.NotFoundReviewException;
import com.six.yummy.global.exception.ValidateReviewException;
import com.six.yummy.order.entity.Order;
import com.six.yummy.order.repository.OrderRepository;
import com.six.yummy.review.entity.Review;
import com.six.yummy.review.repository.ReviewRepository;
import com.six.yummy.review.requestdto.ReviewRequest;
import com.six.yummy.review.responsedto.ReviewResponse;
import com.six.yummy.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final OrderRepository orderRepository;

    public ReviewResponse createReview(Long orderId, User user, ReviewRequest reviewRequest) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        if (!reviewRepository.findByUserAndOrder(user, order).isPresent()) {
            Review review = new Review(order, user, reviewRequest);
            reviewRepository.save(review);

            return new ReviewResponse(review);
        } else {
            throw new DataIntegrityViolationLikeException();
        }
    }

    public List<ReviewResponse> getReviewsByRestaurantId(Long restaurantId) {
        List<Review> reviewList = reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
        if (!reviewList.isEmpty()) {
            List<ReviewResponse> reviewListDTO = reviewList.stream().map(s -> new ReviewResponse(s)).collect(Collectors.toList());
            return reviewListDTO;
        } else {
            throw new NotFoundReviewException();
        }
    }

    public List<ReviewResponse> getReviewsByLike(Long restaurantId) {
        List<Review> reviewList = reviewRepository.findReviewsByRestaurantIdSortedByLikes(restaurantId);
        if (!reviewList.isEmpty()) {
            List<ReviewResponse> reviewListDTO = reviewList.stream().map(s -> new ReviewResponse(s)).collect(Collectors.toList());
            return reviewListDTO;
        } else {
            throw new NotFoundReviewException();
        }
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 없습니다"));
        if (user.getId().equals(review.getUser().getId())) {
            reviewRepository.deleteById(reviewId);
        } else {
            throw new ValidateReviewException();
        }
    }
}
