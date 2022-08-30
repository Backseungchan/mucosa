package com.hanghae99.mocosa.layer.repository;

import com.hanghae99.mocosa.layer.dto.product.RestockListResponseDto;
import com.hanghae99.mocosa.layer.dto.product.SearchRequestDto;
import com.hanghae99.mocosa.layer.dto.product.SearchResponseDto;
import com.hanghae99.mocosa.layer.model.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    QProduct product = QProduct.product;
    QBrand brand = QBrand.brand;
    QCategory parentCategory = QCategory.category1;
    @Override
    public List<RestockListResponseDto> findBySeller(User user) {
        return queryFactory.select(Projections.fields(
                        RestockListResponseDto.class,
                        product.productId,
                        product.name.as("productName")
                ))
                .from(product)
                .where(
                        product.brand.user.userId.eq(user.getUserId()),
                        product.amount.eq(0)
                )
                .fetch();
    }

    @Override
    public Page<SearchResponseDto> findBySearchRequestDto(SearchRequestDto searchRequestDto, Pageable pageable) {
        JPQLQuery<Product> countQuery;
        JPQLQuery<SearchResponseDto> query;

        if(searchRequestDto.getKeyword().equals("ALL")) {
            countQuery = queryFactory.selectFrom(product)
                    .innerJoin(parentCategory).on(product.category.parentCategory.eq(parentCategory.categoryId))
                    .where(
                            categoryEq(searchRequestDto.getCategoryFilter()),
                            priceBetween(searchRequestDto.getMinPriceFilter(),searchRequestDto.getMaxPriceFilter()),
                            reviewAvgGt(searchRequestDto.getReviewFilter())
                    );

            query = queryFactory.select(Projections.fields(
                            SearchResponseDto.class,
                            product.productId,
                            product.name,
                            product.thumbnail,
                            brand.name.as("brandName"),
                            parentCategory.category,
                            product.price,
                            product.amount,
                            product.reviewNum,
                            product.reviewAvg
                    ))
                    .from(product)
                    .innerJoin(brand).on(product.brand.brandId.eq(brand.brandId))
                    .innerJoin(parentCategory).on(product.category.parentCategory.eq(parentCategory.categoryId))
                    .where(
                            categoryEq(searchRequestDto.getCategoryFilter()),
                            priceBetween(searchRequestDto.getMinPriceFilter(),searchRequestDto.getMaxPriceFilter()),
                            reviewAvgGt(searchRequestDto.getReviewFilter())
                    )
                    .orderBy(orderBySort(searchRequestDto.getSort()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
        } else {
            countQuery = queryFactory.selectFrom(product)
                    .innerJoin(brand).on(product.brand.brandId.eq(brand.brandId))
                    .innerJoin(parentCategory).on(product.category.parentCategory.eq(parentCategory.categoryId))
                    .where(
                            keywordContains(searchRequestDto.getKeyword()),
                            categoryEq(searchRequestDto.getCategoryFilter()),
                            priceBetween(searchRequestDto.getMinPriceFilter(),searchRequestDto.getMaxPriceFilter()),
                            reviewAvgGt(searchRequestDto.getReviewFilter())
                    );

            query = queryFactory.select(Projections.fields(
                            SearchResponseDto.class,
                            product.productId,
                            product.name,
                            product.thumbnail,
                            brand.name.as("brandName"),
                            parentCategory.category,
                            product.price,
                            product.amount,
                            product.reviewNum,
                            product.reviewAvg
                    ))
                    .from(product)
                    .innerJoin(brand).on(product.brand.brandId.eq(brand.brandId))
                    .innerJoin(parentCategory).on(product.category.parentCategory.eq(parentCategory.categoryId))
                    .where(
                            keywordContains(searchRequestDto.getKeyword()),
                            categoryEq(searchRequestDto.getCategoryFilter()),
                            priceBetween(searchRequestDto.getMinPriceFilter(),searchRequestDto.getMaxPriceFilter()),
                            reviewAvgGt(searchRequestDto.getReviewFilter())
                    )
                    .orderBy(orderBySort(searchRequestDto.getSort()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
        }

        long totalCount = countQuery.fetchCount();
        List<SearchResponseDto> returnPost = query.fetch();

        return new PageImpl<>(returnPost, pageable,totalCount);
    }

    private BooleanBuilder keywordContains(String keyword) {
        //초기 홈화면 검색
        BooleanBuilder builder = new BooleanBuilder();
        builder
                .or(product.name.contains(keyword))
                .or(product.brand.name.contains(keyword));

        return builder;
    }

    private BooleanExpression categoryEq(String inputCategory) {
        return inputCategory == null ? null : parentCategory.category.eq(inputCategory);
    }

    private BooleanExpression reviewAvgGt(Float reviewAvg) {
        return reviewAvg == null ? null : product.reviewAvg.goe(reviewAvg);
    }

    private BooleanExpression priceBetween(Integer minPriceFilter, Integer maxPriceFilter) {
        if (minPriceFilter == null && maxPriceFilter==null) {
            return null;
        }
        return product.price.between(minPriceFilter, maxPriceFilter);
    }

    private OrderSpecifier<Integer> orderBySort(String sort){
        if (sort.equals("저가순")){
            return product.price.asc();
        }

        if (sort.equals("고가순")){
            return product.price.desc();
        }

        // default 정렬조건 "리뷰순"
        return product.reviewNum.desc();
    }
}