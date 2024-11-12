package com.memo.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memo.common.EncryptUtils;
import com.memo.user.bo.UserBO;
import com.memo.user.entity.UserEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/user")
@RestController
public class UserRestController {
	
	@Autowired
	private UserBO userBO; // 비즈니스 로직을 처리하는 UserBO 객체를 주입

	/**
	 * 아이디 중복 확인 API
	 * @param loginId 사용자 로그인 아이디
	 * @return 중복 여부가 포함된 응답 맵
	 */
	@GetMapping("/is-duplicate-id")
	public Map<String, Object> isDuplicateId(
			// 클라이언트가 보내는 요청에서 "loginId"라는 이름의 파라미터 값을 String loginId 변수에 매핑
			@RequestParam("loginId") String loginId) { 
		
		// userBO.getUserEntityByLoginId(loginId)는 userBO 
		// 객체의 getUserEntityByLoginId 메서드를 호출하여 DB에서 
		// loginId에 해당하는 사용자 정보를 조회합니다.
		UserEntity user = userBO.getUserEntityByLoginId(loginId);
		
		boolean isDuplicate = false; // 기본값은 중복이 아니게 설정 
		
		// user가 null이 아니라면, 동일한 ID를 가진 사용자가 이미 DB에 존재한다는 의미
		if (user != null) {
			// isDuplicate를 true로 설정하여 중복이 있음을 표시
			isDuplicate = true;
		}
		
		// 응답 데이터를 담을 result 맵 변수 생성
		Map<String, Object> result = new HashMap<>(); 
		result.put("code", 200); // 성공 코드 {"key("code")":value(200)}
		result.put("is_duplicate_id", isDuplicate); // 성공 코드 {"key("is_duplicate_id")":value(isDuplicate(T/F))}
		return result; // 값 반환
	}
	
	/**
	 * 회원가입 API
	 * @param loginId 사용자 로그인 아이디
	 * @param password 사용자 비밀번호
	 * @param name 사용자 이름
	 * @param email 사용자 이메일
	 * @return 회원가입 성공 여부를 포함한 응답 맵
	 */
	@PostMapping("/sign-up")
	public Map<String, Object> signUp(
	        @RequestParam("loginId") String loginId,
	        @RequestParam("password") String password,
	        @RequestParam("name") String name,
	        @RequestParam("email") String email) {
	    
	    // 비밀번호 해시 처리 (md5 알고리즘)
	    // 원래 비밀번호는 데이터베이스에 저장할 때 보안 위험이 있으므로,
	    // md5 알고리즘을 사용해 비밀번호를 암호화하여 저장합니다.
	    // 예: 입력된 'aaaa'라는 비밀번호는 '74b8733745420d4d33f80c4663dc5e5' 형식으로 암호화됨.
	    String hashedPassword = EncryptUtils.md5(password);
	    
	    // 사용자 정보를 DB에 저장
	    // 입력받은 로그인 아이디, 해시 처리된 비밀번호, 이름, 이메일 정보를 DB에 저장하기 위해
	    // userBO의 addUser 메서드를 호출하고, 이를 통해 UserEntity 객체가 생성됨.
	    UserEntity user = userBO.addUser(loginId, hashedPassword, name, email);
	    
	    // 응답 데이터를 담을 맵 생성
	    Map<String, Object> result = new HashMap<>();
	    if (user != null) {
	        result.put("code", 200); // 성공 코드
	        result.put("result", "성공");
	    } else {
	        result.put("code", 500); // 실패 코드
	        result.put("error_message", "회원가입에 실패했습니다.");
	    }
	    return result;
	}

	/**
	 * 로그인 API
	 * @param loginId 사용자 로그인 아이디
	 * @param password 사용자 비밀번호
	 * @param request HttpServletRequest 객체
	 * @return 로그인 성공 여부를 포함한 응답 맵
	 */
	@PostMapping("/sign-in")
	public Map<String, Object> signIn(
	        @RequestParam("loginId") String loginId,
	        @RequestParam("password") String password,
	        HttpServletRequest request) {
	    
	    // 입력받은 아이디와 비밀번호로 DB에서 사용자 조회
	    // 로그인 시 입력된 아이디와 비밀번호를 기준으로 userBO의 getUserEntityByLoginIdPassword 메서드가 DB에서
	    // 사용자 정보를 조회합니다. 해당 사용자가 존재하면 UserEntity 객체를 반환하고, 없으면 null 반환.
	    UserEntity user = userBO.getUserEntityByLoginIdPassword(loginId, password);
	    
	    Map<String, Object> result = new HashMap<>();
	    if (user != null) {
	        // 세션에 사용자 정보를 저장 (로그인 상태 유지)
	        // 로그인 상태를 유지하기 위해 세션에 userId, userLoginId, userName을 저장.
	        // 세션에 문제가 발생하는 경우, 로그인 코드에서 오류가 발생할 수 있음.
	    	// userId, userLoginId, userName 자리에 user을 넣을 수 있다. 이 경우 userEntity의 다른 정보까지 가져올 수 있으므로
	    	// 다른 도메인을 만들어서 필요한 값만 채워 사용할 수 있다. 
	        HttpSession session = request.getSession();
	        session.setAttribute("userId", user.getId());       // userId는 UserEntity의 ID 필드에서 가져온 값
	        session.setAttribute("userLoginId", user.getLoginId()); // userLoginId는 로그인 아이디
	        session.setAttribute("userName", user.getName());       // userName은 사용자의 이름
	        result.put("code", 200); // 성공 코드
	        result.put("result", "성공");
	        
	    } else {
	        result.put("code", 300); // 실패 코드 (사용자 없음)
	        result.put("error_message", "존재하지 않는 사용자 입니다.");
	    }
	    return result;
	}
}