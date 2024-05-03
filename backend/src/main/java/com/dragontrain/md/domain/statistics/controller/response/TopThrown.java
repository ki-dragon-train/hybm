package com.dragontrain.md.domain.statistics.controller.response;

import com.dragontrain.md.domain.statistics.service.dto.TopThrownWithCount;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TopThrown {
	private Integer foodId;
	private String name;
	private String imgSrc;

	public static TopThrown createByTopThrownWithCount(TopThrownWithCount topThrownWithCount){
		return TopThrown.builder()
			.foodId(topThrownWithCount.getCategoryDetailId())
			.name(topThrownWithCount.getName())
			.imgSrc(topThrownWithCount.getImgSrc())
			.build();
	}
}
