package com.ark.retailpulse.repository;

import com.ark.retailpulse.dto.product.ProductListDTO;
import com.ark.retailpulse.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

            @Query("SELECT new com.ark.retailpulse.dto.product.ProductListDTO(p.id, p.name,p.description,p.price,p.quantity,p.image)FROM Product p")
            Page<ProductListDTO> findAllWithoutComments(Pageable pageable);
}
