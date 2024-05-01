package com.dragontrain.md.domain.food.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dragontrain.md.common.service.TimeService;
import com.dragontrain.md.domain.food.controller.response.BarcodeInfo;
import com.dragontrain.md.domain.food.domain.Barcode;
import com.dragontrain.md.domain.food.domain.CategoryDetail;
import com.dragontrain.md.domain.food.exception.FoodErrorCode;
import com.dragontrain.md.domain.food.exception.FoodException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class FoodServiceImpl implements FoodService {


	private final CategoryDetailRepository categoryDetailRepository;
	private final BarcodeRepository barcodeRepository;
	private final CrawlService crawlService;
	private final TimeService timeService;

	@Override
	public BarcodeInfo getBarcodeInfo(Long barcode) {

		Barcode barcodeInfo = barcodeRepository.findByBarcodeId(barcode)
			.orElseGet(() -> {
				// 검색
				BarcodeCreate barcodeCreate = crawlService.crawlBarcode(barcode)
					.orElseThrow(() -> new FoodException(FoodErrorCode.UNKNOWN_BARCODE));

				CategoryDetail categoryDetail = categoryDetailRepository.findByKanCode(barcodeCreate.getKanCode())
					.orElseThrow(() -> new FoodException(FoodErrorCode.UNKNOWN_KAN_CODE));

				Barcode createdBarcode = Barcode.create(barcodeCreate.getName(), categoryDetail,
					timeService.localDateTimeNow());
				barcodeRepository.save(createdBarcode);
				return createdBarcode;
			});

		return BarcodeInfo.create(barcodeInfo);
	}
}