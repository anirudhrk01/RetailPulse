package com.ark.retailpulse.repository;

import com.ark.retailpulse.dto.ProductDTO;
import com.ark.retailpulse.dto.ProductListDTO;
import com.ark.retailpulse.model.Comment;
import com.ark.retailpulse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

            @Query("SELECT new com.ark.retailpulse.dto.ProductListDTO(p.id, p.name,p.description,p.price,p.quantity,p.image)FROM Product p")
            List<ProductListDTO> findAllWithoutComments();
}
