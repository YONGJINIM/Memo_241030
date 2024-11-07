package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@Controller
public class PostController {
	
	@Autowired
	private PostBO postBO;

	@GetMapping("/post-list-view")
	public String postListView(Model model, HttpSession session) {
		// 로그인 여부 확인(권한 검사)
		Integer userId = (Integer)session.getAttribute("userId");
		if (userId == null) {
			// 로그인 페이지로 이동
			return "redirect:/user/sign-in-view";
		}
		
		// db select => 로그인 된 사람이 쓴 글
		List<Post> postList = postBO.getPostListByUserId(userId);
				
		// model 담기
		model.addAttribute("postList", postList);
		
		return "post/postList";
	}
	
	@GetMapping("/post-create-view")
	public String postCreateview() {
		return "post/postCreate";
	}
	
	@GetMapping("/post-detail-view")
	public String postDetailView(
			@RequestParam("postId") int postId,
			Model model,
			HttpSession session) {
		
		// db select - postId로 조회, userId로 같이 조회하는 것이 안전
		int userId = (int)session.getAttribute("userId");
		Post post = postBO.getPostByPostIdUserId(postId, userId);
		
		// model 담기 
		model.addAttribute("post",post);		
						
		return "post/postDetail";
	}
}