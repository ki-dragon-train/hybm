package com.dragontrain.md.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", columnDefinition = "bigint", nullable = false)
	private Long userId;

	@Column(name = "email", columnDefinition = "varchar(100)", nullable = false)
	private String email;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "social_login_type", columnDefinition = "char(5)", nullable = false)
	private SocialLoginType socialLoginType;

	@Column(name = "created_at", columnDefinition = "datetime", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "datetime", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", columnDefinition = "datetime")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", columnDefinition = "boolean default false", nullable = false)
	private Boolean isDeleted;

	public static User create(String email, SocialLoginType socialLoginType, LocalDateTime localDateTime) {
		return User.builder()
			.email(email)
			.socialLoginType(socialLoginType)
			.createdAt(localDateTime)
			.updatedAt(localDateTime)
			.isDeleted(false)
			.build();
	}
}