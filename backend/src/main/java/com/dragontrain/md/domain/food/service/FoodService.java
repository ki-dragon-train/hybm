package com.dragontrain.md.domain.food.service;

import com.dragontrain.md.domain.food.controller.request.ReceiptRequest;
import com.dragontrain.md.domain.food.controller.response.ReceiptProducts;
import com.dragontrain.md.domain.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {

	String callGeneralOCR(String imgURL);
	ReceiptProducts callDocumentOCR(MultipartFile imgFile);
	Void registerReceipt(ReceiptRequest receiptRequest, User user);
}
