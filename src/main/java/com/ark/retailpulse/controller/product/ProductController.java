package com.ark.retailpulse.controller.product;

import com.ark.retailpulse.dto.product.ProductDTO;
import com.ark.retailpulse.dto.product.ProductListDTO;
import com.ark.retailpulse.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Service layer dependency for handling product-related operations
    private final ProductService productService;

    /**
     * Endpoint to create a new product. Only accessible to users with ADMIN role.
     * Accepts product details and an optional image as multipart form data.
     *
     * @param productDTO the product details
     * @param image the product image (optional)
     * @return the created product details
     * @throws IOException if there's an error handling the image
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestPart("product") @Valid ProductDTO productDTO,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        logger.info("Creating product: {}", productDTO.getName());
        ProductDTO createdProduct = productService.createProduct(productDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Endpoint to update an existing product. Only accessible to users with ADMIN role.
     * Accepts product ID, updated details, and an optional image as multipart form data.
     *
     * @param id the ID of the product to update
     * @param productDTO the updated product details
     * @param image the updated product image (optional)
     * @return the updated product details
     * @throws IOException if there's an error handling the image
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") @Valid ProductDTO productDTO,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        logger.info("Updating product with ID: {}", id);
        return ResponseEntity.ok(productService.updateProduct(id, productDTO, image));
    }

    /**
     * Endpoint to delete a product. Only accessible to users with ADMIN role.
     *
     * @param id the ID of the product to delete
     * @return an empty response indicating successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to fetch the details of a specific product by its ID.
     * Requires the user to be authenticated.
     *
     * @param id the ID of the product to fetch
     * @return the product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        return ResponseEntity.ok(productService.getProduct(id));
    }

    /**
     * Endpoint to fetch all products with pagination support.
     * Accessible to all users, whether authenticated or not.
     *
     * @param pageable the pagination details
     * @return a paginated list of product summaries
     */
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching all products with pagination: page size = {}", pageable.getPageSize());
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
}
