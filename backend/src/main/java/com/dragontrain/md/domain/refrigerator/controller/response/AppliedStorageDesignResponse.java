package com.dragontrain.md.domain.refrigerator.controller.response;

import com.dragontrain.md.domain.refrigerator.service.dto.AppliedStorageDesign;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AppliedStorageDesignResponse {
	private Integer id;
	private String imgSrc;

	public static AppliedStorageDesignResponse createByAppliedStorageDesign(AppliedStorageDesign appliedStorageDesign){
		return AppliedStorageDesignResponse.builder()
			.id(appliedStorageDesign.getId())
			.imgSrc(appliedStorageDesign.getImgSrc())
			.build();
	}
}
