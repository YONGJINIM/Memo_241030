package com.memo.post;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.bo.PostBO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post") // 모든 게시물 관련 경로의 기본 URL 지정
@RestController // 이 클래스가 REST API 응답을 제공하는 컨트롤러임을 표시
public class PostRestController {

    @Autowired
    private PostBO postBO; // 비즈니스 로직 처리를 위한 PostBO 의존성 주입

    @PostMapping("/create")
    public Map<String, Object> create( // 게시물 생성을 처리하고 결과를 Map 형식으로 반환
            @RequestParam("subject") String subject, // 게시물 제목
            @RequestParam("content") String content, // 게시물 내용
            @RequestParam(value = "file", required = false) MultipartFile file, // 선택적 파일 첨부
            HttpSession session) { // 사용자 정보를 가져오기 위한 세션 객체

        // 세션에서 사용자 정보 가져오기
        int userId = (int) session.getAttribute("userId"); // 사용자 ID 가져오기
        String userLoginId = (String) session.getAttribute("userLoginId"); // 사용자 로그인 ID 가져오기
        
        // 게시물 정보를 데이터베이스에 삽입하고 삽입된 행 수를 반환
        int rowCount = postBO.addPost(userId, userLoginId, subject, content, file);

        // 응답 데이터를 담을 Map 생성
        Map<String, Object> result = new HashMap<>();
        if (rowCount > 0) { // 삽입이 성공한 경우
            result.put("code", 200); // 상태 코드: 200 (성공)
            result.put("result", "성공"); // 성공 메시지
        } else { // 삽입이 실패한 경우
            result.put("code", 500); // 상태 코드: 500 (서버 오류)
            result.put("error_message", "글을 저장하는데 실패했습니다. 관리자에게 문의해주세요."); // 에러 메시지
        }

        return result; // 최종 응답 반환
    }
}
