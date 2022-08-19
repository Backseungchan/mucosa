package com.hanghae99.mocosa.layer.repository;

import com.hanghae99.mocosa.layer.dto.product.RestockListResponseDto;
import com.hanghae99.mocosa.layer.dto.product.SearchRequestDto;
import com.hanghae99.mocosa.layer.dto.product.SearchResponseDto;
import com.hanghae99.mocosa.layer.model.QCategory;
import com.hanghae99.mocosa.layer.model.QProduct;
import com.hanghae99.mocosa.layer.model.User;
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
    QCategory category = QCategory.category1;

    @Override
    public Page<SearchResponseDto> findBySearchRequestDto(SearchRequestDto searchRequestDto, Pageable pageable) {
        JPQLQuery<SearchResponseDto> query = queryFactory.select(Projections.fields(
                        SearchResponseDto.class,
                        product.productId,
                        product.name,
                        product.thumbnail,
                        product.brand.name.as("brandName"),
                        product.category.category,
                        product.price,
                        product.amount,
                        product.reviewNum,
                        product.reviewAvg
                ))
                .from(product)
                .join(category).on(product.category.parentCategory.eq(category.categoryId))
                .where(
                        keywordContains(searchRequestDto.getKeyword()),
                        categoryEq(searchRequestDto.getCategoryFilter()),
                        priceBetween(searchRequestDto.getMinPriceFilter(),searchRequestDto.getMaxPriceFilter()),
                        reviewAvgGt(searchRequestDto.getReviewFilter())
                )
                .orderBy(orderBySort(searchRequestDto.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<SearchResponseDto> returnPost = query.fetchResults();

        return new PageImpl<>(returnPost.getResults(), pageable, returnPost.getTotal());
    }

    private BooleanBuilder keywordContains(String keyword) {
        //초기 홈화면 검색
        if (keyword.equals("ALL"))
            return null;
        BooleanBuilder builder = new BooleanBuilder();
        builder
                .or(product.name.contains(keyword))
                .or(product.brand.name.contains(keyword));

        return builder;
    }

    private BooleanBuilder categoryEq(String inputCategory) {
        if(inputCategory==null)
            return null;

        BooleanBuilder builder = new BooleanBuilder();
        builder
                .or(product.category.category.eq(inputCategory))
                .or(category.category.eq(inputCategory));

        return builder;
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
}
