package com.ark.retailpulse.mapper;

import com.ark.retailpulse.dto.CommentDTO;
import com.ark.retailpulse.dto.ProductDTO;
import com.ark.retailpulse.model.Comment;
import com.ark.retailpulse.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

//    @Mapping(target ="" , source =" " )
    ProductDTO toDTO(Product product);

//    @Mapping(target ="" , source =" " )
    Product toEntity(ProductDTO productDTO);

    CommentDTO toDTO(Comment comment);

    Comment toEntity(CommentDTO commentDTO);


}
