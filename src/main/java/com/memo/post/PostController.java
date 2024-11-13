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
	    // 세션에서 userId 속성을 가져옴. 이 userId가 null이면 로그인이 안 된 상태이므로 접근을 허용하지 않음
	    // getAttribute가 Object 타입이므로 다운캐스팅을 진행하며, userId가 null일 수 있기 때문에 Integer 사용
	    Integer userId = (Integer) session.getAttribute("userId"); // 브레이크 포인트 걸어서 값(userId)이 잘 들어왔는지 확인 
	    if (userId == null) {
	        // 로그인이 안 된 상태이므로 로그인 페이지로 리다이렉트
	        return "redirect:/user/sign-in-view";
	    }
	    
	    // db select => 로그인 된 사람이 쓴 글
	    // 로그인한 사용자의 userId를 기반으로 DB에서 해당 사용자가 작성한 게시글 목록을 조회
	    List<Post> postList = postBO.getPostListByUserId(userId);
	            
	    // model에 postList 담기
	    // 조회한 게시글 목록을 model 객체에 담아서 View에 전달
	    model.addAttribute("postList", postList);
	    
	    // 게시글 목록 페이지로 이동
	    return "post/postList";
	}
	/**
	 * 글쓰기 화면
	 * 사용자가 /post-create-view 경로로 접속하면 post/postCreate.html 파일을 보여주도록 설정
	 * @return
	 */
	@GetMapping("/post-create-view")
	public String postCreateview() {
		return "post/postCreate";
	}
	/**
	 * 글 상세 화면
	 * @param postId
	 * @param model
	 * @param session
	 * @return
	 */
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