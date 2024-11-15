package com.memo.interceptor;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws IOException {
	
		// 요청 url path 
		String uri = request.getRequestURI();
		log.info("[@@@@@@@@@@@ preHandle] uri:{}", uri);
		
		// 로그인 여부 => 세션에서 꺼내온다 
		HttpSession session = request.getSession();
		Integer userId = (Integer)session.getAttribute("userId");
		
		// /post로 시작하고 && 비로그인이다 => 로그인 페이지로 이동, 컨트롤러 수행 방지
		if(uri.startsWith("/post") && userId == null) {
			
			// redirect
			response.sendRedirect("/user/sign-in-view");
			
			// 컨트롤러 수행 방지 
			return false; // 반드시 막아줘야함 
		}
		
		// user로 시작 && 로그인 => 글 목록 이동, 컨트롤러 방지
		// 반드시 여러 화면을 통해서 확인해야함 
		if(uri.startsWith("/user") && userId != null) {
			response.sendRedirect("/post/post-list-view");
			return false; // 월래 가려했던 컨트롤러 수행방지 
		}
		
		
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView mav) {
		// view, model이 있다는 것은 html(랜더링)이 아직 안된 상태이다.
		// 뷰에 뿌리기 전에 셋팅을 하려고 한다면 사용하는 것을 추천 
		log.info("[$$$$$$$$$$$$$$$$ postHandle]");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			Exception ex) {
	
		// html이 렌더링이 끝난 상태이다. 
		log.info("[$$$$$$$$$$$$$$$$ afterCompletion]");
	}
}
