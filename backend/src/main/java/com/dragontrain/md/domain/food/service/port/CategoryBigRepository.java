package com.dragontrain.md.domain.food.service.port;

import java.util.List;

import com.dragontrain.md.domain.food.domain.CategoryBig;
import com.dragontrain.md.domain.statistics.service.dto.BigCategoryPriceInfo;

public interface CategoryBigRepository {

	List<CategoryBig> findAll();

	List<BigCategoryPriceInfo> findAllBigGroupAndSpend(Long refrigeratorId, Integer year, Integer month);
}
