package com.dragontrain.md.domain.notice.controller;

import com.dragontrain.md.common.config.constraint.Path;
import com.dragontrain.md.domain.notice.controller.request.StoreFCMTokenRequest;
import com.dragontrain.md.domain.notice.controller.response.AllNoticeResponse;
import com.dragontrain.md.domain.notice.controller.response.HasnewNoticeResponse;
import com.dragontrain.md.domain.notice.service.NoticeService;
import com.dragontrain.md.domain.user.domain.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

	private final NoticeService noticeService;

	@GetMapping
	public ResponseEntity<AllNoticeResponse> findAllNotice(@AuthenticationPrincipal User user, @NotNull Pageable pageable) {
		return ResponseEntity.ok(noticeService.findAllNotDeletedNotice(user, pageable));
	}

	@GetMapping("/hasnew")
	public ResponseEntity<HasnewNoticeResponse> existsNewNotice(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(noticeService.existsNewNotice(user));
	}

	@DeleteMapping("/{deleteType}")
	public ResponseEntity<Void> changeFoodStatusOnNotice(@AuthenticationPrincipal User user,
														 @PathVariable @Path(candidates = {"eaten", "thrown"}, message = "eaten, thrown의 값만 입력해주세요") String deleteType,
														 @RequestParam @NotNull @NotEmpty(message = "noticeId를 1개 이상 보내야 합니다.")  Long[] noticeId) {
		noticeService.deleteFood(user, deleteType, noticeId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteNotice(
		@AuthenticationPrincipal User user,
		@RequestParam @NotNull Long noticeId
	) {
		noticeService.deleteNotice(user, noticeId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/all")
	public ResponseEntity<Void> deleteAllNotice(
		@AuthenticationPrincipal User user
	) {
		noticeService.deleteAllNotice(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/fcm")
	public ResponseEntity<Void> saveFCMToken(
		@AuthenticationPrincipal User user,
		@RequestBody StoreFCMTokenRequest request
	) {
		noticeService.saveFCMToken(user, request.getToken());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
