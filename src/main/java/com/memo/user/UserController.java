package com.memo.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	/**
	 * 회원가입 화면으로 이동
	 * @return 회원가입 화면 템플릿 이름
	 */
	@GetMapping("/sign-up-view") // 유저가 브라우저에 입력 
	public String signUpView() {
		// "user/signUp" 템플릿을 반환하여 회원가입 화면을 구성 (전체 레이아웃 중 가운데 조각만 포함)
		return "user/signUp"; // 템플릿/유저/사인업.html 반환 
	}
	
	/**
	 * 로그인 화면으로 이동
	 * @return 로그인 화면 템플릿 이름
	 */
	@GetMapping("/sign-in-view")
	public String signInView() {
		return "user/signIn"; // 로그인 화면 템플릿 반환
	}
	
	/**
	 * 로그아웃 처리
	 * @param session 현재 사용자 세션 객체
	 * @return 회원가입 화면으로 리다이렉트
	 */
	@GetMapping("/sign-out")
	public String signOut(HttpSession session) { 
		// 세션에서 사용자 정보를 삭제하여 로그아웃 처리
		session.removeAttribute("userId");
		session.removeAttribute("userLoginId");
		session.removeAttribute("userName");
		
		// 회원가입 화면으로 리다이렉트
		return "redirect:/user/sign-up-view";
	}
}
