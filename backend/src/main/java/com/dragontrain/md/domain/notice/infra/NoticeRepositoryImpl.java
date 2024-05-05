package com.dragontrain.md.domain.notice.infra;

import com.dragontrain.md.domain.notice.domain.Notice;
import com.dragontrain.md.domain.notice.service.port.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class NoticeRepositoryImpl implements NoticeRepository {
	private final NoticeJpaRepository noticeJpaRepository;
	@Override
	public Slice<Notice> findAllNotDeletedNotice(Long refrigeratorId, Pageable pageable) {
		return noticeJpaRepository.findAllNotDeletedNotice(refrigeratorId, pageable);
	}
}
