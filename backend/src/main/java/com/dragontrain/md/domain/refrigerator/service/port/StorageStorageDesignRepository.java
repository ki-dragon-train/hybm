package com.dragontrain.md.domain.refrigerator.service.port;

import com.dragontrain.md.domain.refrigerator.controller.response.AppliedStorageDesign;
import com.dragontrain.md.domain.refrigerator.controller.response.StorageDesignResponse;
import com.dragontrain.md.domain.refrigerator.domain.StorageStorageDesign;

import java.util.List;

public interface StorageStorageDesignRepository {
	List<StorageDesignResponse> findAllStorageDesign(Long refrigeratorId);
	List<AppliedStorageDesign> findAllAppliedStorageDesign(Long refrigeratorId);
	void save(StorageStorageDesign storageStorageDesign);
}
