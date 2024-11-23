package com.ark.retailpulse.service;

import com.ark.retailpulse.dto.ProductDTO;
import com.ark.retailpulse.dto.ProductListDTO;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.mapper.ProductMapper;
import com.ark.retailpulse.model.Product;
import com.ark.retailpulse.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final String UPLOAD_DIR = "src/main/resources/images/";

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) throws IOException {
        Product product = productMapper.toEntity(productDTO);
        if(image != null && !image.isEmpty()){
            String filename = saveImage(image);
            product.setImage("/images/"+filename);
        }
        Product saveProduct = productRepository.save(product);
        return productMapper.toDTO(saveProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id,ProductDTO productDTO, MultipartFile image) throws IOException{
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());

        if(image != null && !image.isEmpty()){
            String filename = saveImage(image);
            existingProduct.setImage("/images/"+filename);
        }
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public ProductDTO getProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        return productMapper.toDTO(product);
    }

    public List<ProductListDTO> getAllProducts(){

           return productRepository.findAllWithoutComments();
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString()+"_"+image.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR +fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return fileName;
    }


}























