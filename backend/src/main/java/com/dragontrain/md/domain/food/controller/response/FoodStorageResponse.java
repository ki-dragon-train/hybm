package com.dragontrain.md.domain.food.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodStorageResponse {
	private List<FoodStorage> fresh;
	private List<FoodStorage> warning;
	private List<FoodStorage> danger;
	private List<FoodStorage> rotten;

	public static FoodStorageResponse create(List<FoodStorage> fresh,
											 List<FoodStorage> warning,
											 List<FoodStorage> danger,
											 List<FoodStorage> rotten) {

		return FoodStorageResponse.builder()
			.fresh(fresh)
			.warning(warning)
			.danger(danger)
			.rotten(rotten).build();
	}
}