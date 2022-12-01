package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;


import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory jpaQueryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all" , searchDateType) || searchDateType == null) {
            return null;
        } else if(StringUtils.equals("1d" , searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w" , searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m" , searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m" , searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy , String searchQuery) {

        if(StringUtils.equals("itemNm" , searchBy)) {
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        } else if(StringUtils.equals("createBy" ,searchQuery)) {
            return QItem.item.createBy.like("%"+searchQuery+"%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QueryResults<Item> results = jpaQueryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()) ,
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()) ,
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()) // 데이터를 가지고 올 시작 인덱스를 지정합니다.
                .limit(pageable.getPageSize()) // 한 번에 가지고 올 최대 개수를 지정합니다.
                .fetchResults(); // 조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환합니다. 2번의 쿼리문이 실행됩니다.

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content , pageable , total); // 조회한 Page 클래스의 구현체인 PageImpl 객체로 반환합니다.
    }

    private BooleanExpression itemNmLike(String searchQuery) { // 검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환합니다.

        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = jpaQueryFactory.select(
                new QMainItemDto( // QMainItemDto의 생성자에 반환할 값들을 넣어줍니다. @QueryProjection을 사용하면 DTO로 바로 조회가 가능합니다. 엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있습니다.
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        itemImg.imgUrl,
                        item.price
                )
        ).from(itemImg)
                .join(itemImg.item ,item) // itemImg와 item을 내부 조인합니다.
                .where(itemImg.repImgYn.eq("Y")) // 상품 이미지의 경우 대표 상품 이미지만 불러옵니다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content , pageable , total);
    }
}
