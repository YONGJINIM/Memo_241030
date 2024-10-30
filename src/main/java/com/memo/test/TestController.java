package com.memo.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.memo.post.mapper.PostMapper;

@Controller
public class TestController {

	@Autowired
	PostMapper postMapper;
	
	
	@ResponseBody
	@GetMapping("/test1")
	public String test1() {
		return "<h1>String Test</h1>";
	}
	
	@ResponseBody
	@GetMapping("/test2")
	public Map<String, Object> test2(){
		Map<String, Object> map = new HashMap<>();
		map.put("aaa", 111);
		map.put("bbb", 222);
		map.put("ccc", 333);
		return map;
	}
	@GetMapping("/test3")
	public String test3() {
		return "test/test";
	}
	@ResponseBody
	@GetMapping("/test4")
	public List<Map<String , Object>> test4(){ // 하나의 행은 Map에 담긴다
		return postMapper.selectPostList();
	}
	
}
