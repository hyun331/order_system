package com.beyond.order_system.product.service;

import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.dto.ProductResDto;
import com.beyond.order_system.product.dto.ProductSaveRepDto;
import com.beyond.order_system.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product productCreate(ProductSaveRepDto productSaveRepDto) {
        MultipartFile image = productSaveRepDto.getProductImage();
        Product product = null;
        try{
            product = productRepository.save(productSaveRepDto.toEntity());

            //이미지 파일 저장시 byte로
            byte[] bytes = image.getBytes();
            //랜덤 이름으로 저장됨
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/", product.getId()+"_"+image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());   //더티체크를 통해 변경감지함 -> 다시 save하지 않아도 됨.
        }catch (IOException e){
            throw new RuntimeException("임미지 저장 실패");
        }

        return product;
    }

    public Page<ProductResDto> productList(Pageable pageable){
        return productRepository.findAll(pageable).map(a->a.fromEntity());
    }
}
