package com.dragontrain.md.common.config.utils;

import java.util.Arrays;

import org.springframework.http.ResponseCookie;
import org.springframework.util.ObjectUtils;

import jakarta.servlet.http.Cookie;

public class CookieUtils {

	private static final String COOKIE_KEY_REFRESH_TOKEN = "refresh_token";
	private static final String COOKIE_KEY_ACCESS_TOKEN = "access_token";
	private static final String COOKIE_ATTRIBUTE_NAME_SAME_SITE = "SameSite";
	private static final String COOKIE_SAME_SITE_NONE = "none";
	private static final String COOKIE_REFRESH_TOKEN_PATH = "/";
	private static final String COOKIE_ACCESS_TOKEN_PATH = "/";

	public static Cookie makeCookie(String key, String value, String path, int maxAge) {
		Cookie cookie = new Cookie(key, value);
		cookie.setAttribute(COOKIE_ATTRIBUTE_NAME_SAME_SITE, COOKIE_SAME_SITE_NONE);
		cookie.setPath(path);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(maxAge);
		return cookie;
	}

	public static Cookie deleteCookie(String key, String value, String path) {
		return makeCookie(key, value, path, 0);
	}

	public static Cookie deleteAccessTokenCookie() {
		return makeAccessTokenCookie("", 0);
	}

	public static Cookie deleteRefreshTokenCookie() {
		return makeRefreshTokenCookie("", 0);
	}

	public static Cookie makeRefreshTokenCookie(String value, int maxAge) {
		return makeCookie(COOKIE_KEY_REFRESH_TOKEN, value, COOKIE_REFRESH_TOKEN_PATH, maxAge);
	}

	public static Cookie makeAccessTokenCookie(String value, int maxAge) {
		return makeCookie(COOKIE_KEY_ACCESS_TOKEN, value, COOKIE_ACCESS_TOKEN_PATH, maxAge);
	}

	public static Cookie findAccessTokenCookie(Cookie[] cookies) {
		if (ObjectUtils.isEmpty(cookies))
			return null;

		return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(COOKIE_KEY_ACCESS_TOKEN))
			.findAny()
			.orElse(null);
	}

	public static Cookie findRefreshToken(Cookie[] cookies) {
		if (ObjectUtils.isEmpty(cookies))
			return null;

		return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(COOKIE_KEY_ACCESS_TOKEN))
			.findAny()
			.orElse(null);
	}
}
