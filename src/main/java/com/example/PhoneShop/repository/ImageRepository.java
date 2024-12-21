package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

}
