package com.dragontrain.md.domain.food.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryInfoDetail {
	private Integer categoryId;
	private String name;
	private String categoryImgSrc;

	public static CategoryInfoDetail create(Integer categoryId, String name, String categoryImgSrc) {
		return CategoryInfoDetail.builder()
			.categoryId(categoryId)
			.name(name)
			.categoryImgSrc(categoryImgSrc).build();
	}
}
