package com.ark.retailpulse.service.product;

import com.ark.retailpulse.dto.product.ProductDTO;
import com.ark.retailpulse.dto.product.ProductListDTO;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.mapper.ProductMapper;
import com.ark.retailpulse.model.Product;
import com.ark.retailpulse.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // Logger for this service class
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final String UPLOAD_DIR = "src/main/resources/images/";

    /**
     * Creates a new product and saves it to the repository.
     * If an image is provided, it is saved to the specified directory.
     *
     * @param productDTO the product details
     * @param image the product image file
     * @return the saved product as a DTO
     * @throws IOException if there is an error saving the image
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) throws IOException {
        log.info("Creating a new product: {}", productDTO.getName());

        // Convert DTO to entity
        Product product = productMapper.toEntity(productDTO);

        // Save image if provided
        if (image != null && !image.isEmpty()) {
            String filename = saveImage(image);
            product.setImage("/images/" + filename);
        }

        // Save the product to the repository
        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());

        return productMapper.toDTO(savedProduct);
    }

    /**
     * Updates an existing product by its ID.
     * If an image is provided, it is updated as well.
     *
     * @param id the ID of the product to update
     * @param productDTO the updated product details
     * @param image the new product image file
     * @return the updated product as a DTO
     * @throws IOException if there is an error saving the image
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, MultipartFile image) throws IOException {
        log.info("Updating product with ID: {}", id);

        // Find the existing product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Update product details
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());

        // Update image if provided
        if (image != null && !image.isEmpty()) {
            String filename = saveImage(image);
            existingProduct.setImage("/images/" + filename);
        }

        // Save the updated product
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated with ID: {}", updatedProduct.getId());

        return productMapper.toDTO(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        // Check if the product exists
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        // Delete the product
        productRepository.deleteById(id);
        log.info("Product deleted with ID: {}", id);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return the product as a DTO
     */
    public ProductDTO getProduct(Long id) {
        log.info("Fetching product with ID: {}", id);

        // Find the product by ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return productMapper.toDTO(product);
    }

    /**
     * Retrieves all products without comments, with pagination.
     *
     * @param pageable pagination details
     * @return a page of products without comments
     */
    public Page<ProductListDTO> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination");

        return productRepository.findAllWithoutComments(pageable);
    }

    /**
     * Saves the product image to the server's file system.
     *
     * @param image the image file to save
     * @return the saved image file's name
     * @throws IOException if there is an error saving the image
     */
    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent()); // Ensure the directory exists
        Files.write(path, image.getBytes()); // Save the file
        log.info("Image saved with filename: {}", fileName);
        return fileName;
    }
}
