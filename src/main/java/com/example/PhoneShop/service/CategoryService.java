package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.CategoryMapper;
import com.example.PhoneShop.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public CategoryResponse create(CreateCategoryRequest request){

        if(categoryRepository.existsByName(request.getName())){
            log.warn("Category already exists: {}", request.getName());
            throw new AppException(HttpStatus.FOUND, "Category already exists");
        }

        Category category = categoryMapper.toCategory(request);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll(){
        var categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public void delete(String id){
        categoryRepository.deleteById(id);
    }

}
