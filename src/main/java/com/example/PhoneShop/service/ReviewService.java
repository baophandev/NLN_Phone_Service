package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.ReviewRequest.CreateReviewRequest;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.ReviewResponse;
import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.entities.Review;
import com.example.PhoneShop.entities.ReviewImage;
import com.example.PhoneShop.entities.User;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.ReviewMapper;
import com.example.PhoneShop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    OrderRepository orderRepository;
    ReviewImageRepository reviewImageRepository;
    ReviewMapper reviewMapper;
    UserRepository userRepository;

    @PreAuthorize("hasAnyRole('USER')")
    public ReviewResponse create(CreateReviewRequest request, List<MultipartFile> files) throws IOException {

        Product product = productRepository.findById(request.getPrdId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist!"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User does not exist!"));

        Review review = new Review();
        review.setComment(request.getComment());
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setUser(user);

        List<ReviewImage> images = new ArrayList<>();
        for (MultipartFile file : files){
            ReviewImage image = ReviewImage.builder()
                    .imageType(file.getContentType())
                    .data(file.getBytes())
                    .review(review)
                    .build();
            images.add(image);
        }

        review.setImages(images);

        reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(review.getId())
                .prdId(review.getProduct().getId())
                .displayName(review.getUser().getDisplayName())
                .comment(review.getComment())
                .rating(review.getRating())
                .images(review.getImages())
                .build();
    }

    public CustomPageResponse<ReviewResponse> getAllByPrdId(String productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);

        List<ReviewResponse> reviewResponses = reviews.getContent()
                .stream().map(reviewMapper::toReviewResponse).toList();

        return CustomPageResponse.<ReviewResponse>builder()
                .pageNumber(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .content(reviewResponses)
                .build();
    }

}
