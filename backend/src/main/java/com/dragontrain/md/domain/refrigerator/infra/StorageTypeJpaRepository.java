package com.dragontrain.md.domain.refrigerator.infra;

import com.dragontrain.md.domain.refrigerator.domain.StorageType;
import com.dragontrain.md.domain.refrigerator.domain.StorageTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageTypeJpaRepository extends JpaRepository<StorageType, StorageTypeId> {
	Optional<StorageType> findById(StorageTypeId id);
}
