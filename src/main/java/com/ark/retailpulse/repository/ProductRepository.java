package com.ark.retailpulse.repository;

import com.ark.retailpulse.model.Comment;
import com.ark.retailpulse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

}
