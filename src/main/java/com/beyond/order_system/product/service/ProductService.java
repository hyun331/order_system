package com.beyond.order_system.product.service;

import com.beyond.order_system.common.service.StockInventoryService;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.dto.ProductResDto;
import com.beyond.order_system.product.dto.ProductSaveRepDto;
import com.beyond.order_system.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional
public class ProductService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final ProductRepository productRepository;
    private final StockInventoryService stockInventoryService;
    private final S3Client s3Client;
    @Autowired
    public ProductService(ProductRepository productRepository, StockInventoryService stockInventoryService, S3Client s3Client) {
        this.productRepository = productRepository;
        this.stockInventoryService = stockInventoryService;
        this.s3Client = s3Client;
    }

    public Product productCreate(ProductSaveRepDto productSaveRepDto) {
        MultipartFile image = productSaveRepDto.getProductImage();
        Product product = null;
        try{
            product = productRepository.save(productSaveRepDto.toEntity());
            //상품을 등록하면 redis에도 등록해주면 됨
            if(productSaveRepDto.getName().contains("sale")){
                stockInventoryService.increaseStock(product.getId(), product.getStockQuantity());
            }
            //이미지 파일 저장시 byte로
            byte[] bytes = image.getBytes();
            //랜덤 이름으로 저장됨
            Path path = Paths.get("C:/Users/신승현/Desktop/tmp/", product.getId()+"_"+image.getOriginalFilename());
//            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/", product.getId()+"_"+image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());   //더티체크를 통해 변경감지함 -> 다시 save하지 않아도 됨.
        }catch (IOException e){
            throw new RuntimeException("이미지 저장 실패");
        }

        return product;
    }



    //aws에 파일 업로드
    public Product productAwsCreate(ProductSaveRepDto productSaveRepDto) {
        MultipartFile image = productSaveRepDto.getProductImage();
        Product product = null;
        try{
            product = productRepository.save(productSaveRepDto.toEntity());

            //이미지 파일 저장시 byte로
            byte[] bytes = image.getBytes();

            String fileName = product.getId()+"_"+image.getOriginalFilename();
            //////////////////////path  변경하기/////////////////////
//            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/", fileName);
            Path path = Paths.get("C:/Users/신승현/Desktop/tmp/", fileName);


            //local pc에 임시 저장
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            //aws에 pc에 저장된 파일을 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
            //s3에 올라가있는 파일 경로 찾아오기
            String s3Path = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();

            //우리가 정한 파일 이름이 아니라 http:// 이런 url이 저장됨
            product.updateImagePath(s3Path);   //더티체크를 통해 변경감지함 -> 다시 save하지 않아도 됨.
        }catch (IOException e){
            throw new RuntimeException("임미지 저장 실패");
        }

        return product;
    }

    public Page<ProductResDto> productList(Pageable pageable){
        return productRepository.findAll(pageable).map(a->a.fromEntity());
    }
}
