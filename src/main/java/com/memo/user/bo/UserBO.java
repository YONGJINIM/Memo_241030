package com.memo.user.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memo.user.entity.UserEntity;
import com.memo.user.repository.UserRepository;

@Service
public class UserBO {

	@Autowired
	private UserRepository userRepository;
	
	// 컨트롤러한테
	// input: loginId
	// output: UserEntity or null
	public UserEntity getUserEntityByLoginId(String loginId) {
		return userRepository.findByLoginId(loginId);
	}
	
	// input: 파라미터4개
	// output: UserEntity
	public UserEntity addUser(String loginId, String password,
			String name, String email) {
		
		return userRepository.save(
			UserEntity.bu	
			)
				
	}
}