package com.hanghae99.mocosa.test;

import com.hanghae99.mocosa.layer.model.Brand;
import com.hanghae99.mocosa.layer.model.Category;
import com.hanghae99.mocosa.layer.model.Product;
import com.hanghae99.mocosa.layer.model.User;
import com.hanghae99.mocosa.layer.repository.BrandRepository;
import com.hanghae99.mocosa.layer.repository.CategoryRepository;
import com.hanghae99.mocosa.layer.repository.ProductRepository;
import com.hanghae99.mocosa.layer.repository.UserRepository;
import com.hanghae99.mocosa.layer.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TestDataRunner implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductService productService;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        User testUser1 = new User(1L,"test1@test.com", "1234");
        User testUser2 = new User(2L,"test2@test.com", "1234");
        User testUser3 = new User(3L,"test3@test.com", "1234");
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);

        // Brand 생성
        Brand testBrand1 = new Brand(1L, "무신사 무탠다드", testUser1);
        Brand testBrand2 = new Brand(2L, "칼하트", testUser1);
        Brand testBrand3 = new Brand(3L, "마크곤잘레스", testUser2);
        Brand testBrand4 = new Brand(3L, "비바스튜디오", testUser2);
        Brand testBrand5 = new Brand(3L, "디스이즈네버댓", testUser3);
        Brand testBrand6 = new Brand(3L, "커버낫", testUser3);
        brandRepository.save(testBrand1);
        brandRepository.save(testBrand2);
        brandRepository.save(testBrand3);
        brandRepository.save(testBrand4);
        brandRepository.save(testBrand5);
        brandRepository.save(testBrand6);


        // Category 생성
        Category testCategory1 = new Category(1L, "상의", null);
        Category testCategory2 = new Category(2L, "바지", null);
        Category testCategory3 = new Category(3L, "반소매 티셔츠", 1L);
        Category testCategory4 = new Category(4L, "긴소매 티셔츠", 1L);
        Category testCategory5 = new Category(5L, "데님 팬츠", 2L);
        Category testCategory6 = new Category(6L, "코튼 팬츠", 2L);
        categoryRepository.save(testCategory1);
        categoryRepository.save(testCategory2);
        categoryRepository.save(testCategory3);
        categoryRepository.save(testCategory4);
        categoryRepository.save(testCategory5);
        categoryRepository.save(testCategory6);

        // Product 생성
        Product testProduct1 = new Product(1L, testBrand1, "릴렉스 핏 크루 넥 반팔 티셔츠", "image.png", testCategory1, 10690, 100, 69058, 4.8F);
        Product testProduct2 = new Product(2L, testBrand2, "K87 워크웨어 포켓 반팔티셔츠 (20colors)", "image2.png", testCategory3, 75030, 0, 590, 4.5F);
        Product testProduct3 = new Product(3L, testBrand3, "사인 로고 후드 그레이", "image3.png", testCategory4, 25000, 79, 234, 4.2F);
        Product testProduct4 = new Product(4L, testBrand4, "DRAWING DENIM PANTS", "image4.png", testCategory5, 37000, 50, 22233, 3.7F);
        Product testProduct5 = new Product(5L, testBrand5, "Cargo Pant Dusty Blue", "image5.png", testCategory6, 33400, 44, 990, 2.5F);
        Product testProduct6 = new Product(6L, testBrand6, "럭비 스트라이프 티셔츠 네이비", "image6.png", testCategory2, 13020, 9999, 40, 3.8F);
        Product testProduct7 = new Product(7L, testBrand1, "릴렉스 핏 크루 넥 긴팔 티셔츠", "image.png", testCategory1, 20000, 100, 5000, 3F);

        productRepository.save(testProduct1);
        productRepository.save(testProduct2);
        productRepository.save(testProduct3);
        productRepository.save(testProduct4);
        productRepository.save(testProduct5);
        productRepository.save(testProduct6);
        productRepository.save(testProduct7);
    }
}