package com.dragontrain.md.domain.food.exception;

import org.springframework.http.HttpStatus;

import com.dragontrain.md.common.config.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FoodErrorCode implements ErrorCode {

	UNKNOWN_BARCODE(HttpStatus.NOT_FOUND, "알 수 없는 바코드입니다. 직접 정보를 등록해주세요"),
	UNKNOWN_KAN_CODE(HttpStatus.NOT_FOUND, "알 수 없는 칸코드입니다"),
	REFRIGERATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "냉장고 정보를 찾을 수 없습니다"),
	INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "날짜 형식이 맞지 않습니다"),
	NOT_MY_FOOD(HttpStatus.BAD_REQUEST, "내 냉장고에 있는 식품이 아닙니다"),
	STORAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "저장고 종류를 찾을 수 없습니다"),
	FOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "식품 정보를 찾을 수 없습니다"),
	CATEGORY_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "소분류를 찾을 수 없습니다"),
	EXPIRATION_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "예상 소비기한 정보가 존재하지 않습니다. 직접 기입해주세요");

	private final HttpStatus httpStatus;
	private final String errorMessage;

	public String getErrorName() {
		return this.name();
	}
}
