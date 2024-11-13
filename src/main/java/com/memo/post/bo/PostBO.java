package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // slf4j
public class PostBO {
	//private Logger log = LoggerFactory.getLogger(postBO.class);
	//private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId);
	}
	
	// input: userId, userLoginId , subject, content, file
	// output: int(성공한 행 개수)
	public int addPost(int userId, 
			String userLoginId , 
			String subject, 
			String content, 
			MultipartFile file) {
		
		String imagePath = null;
		
		// file to imagePath
		// file가 있을 때만 업로드를 진행
		if(file != null) {
			imagePath = fileManagerService.uploadFile(file, userLoginId);
		}
		
		return postMapper.insertPost(userId, subject, content, imagePath);
	}
	
	public Post getPostByPostIdUserId(int postId, int userId) {
		return postMapper.selectPostByPostIdUserId(postId, userId);
	}
	// input: userLoginId(파일), postId, userId, subject, content, file  
	// output : void
	public void updatePostByPostIdUserId(
			int postId,
			int userId,
			String userLoginId,
			String content,
			MultipartFile file) {
		
		// 기존글을 가져온다. (1. 이미지 교체시 기존 이미지 삭제를 위해, 2. 업데이트 대상 존재 여부 확인
		Post post = postMapper.selectPostByPostIdUserId(postId, userId);
		if(post == null) {
			log.info("[######글 수정] post is null. postId:{} , userId:{}", postId , userId);
			return;
		}		
		// 파일이 존재하면 새이미지를 업로드 한다. 
		String imagePath = null;
		if(file != null) {
			// 새 이미지를 업로드 
			// 업로드가 성공하면 기존에 있는 이미지를 삭제 
			imagePath = fileManagerService.uploadFile(file, userLoginId);
			
			// 업로드 성공 && 기존이미지 존재시 삭제 
			if(imagePath != null && post.getImagePath() != null){
				// 서버에 존재하는 폴더 및 이미지 제거 
				fileManagerService.deleteFile(post.getImagePath());
			}
		}		
		// DB update 
		postMapper.updatePostByPostId(postId, userLoginId, content, imagePath);
		
	}
}