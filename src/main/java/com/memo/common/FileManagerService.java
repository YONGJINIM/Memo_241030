package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileManagerService {
    // 실제 업로드된 이미지가 저장될 서버 경로
    // 경로 마지막에 슬래시를 붙여준다 없으면 경로를 제대로 인식하지 못한다. 
    public static final String FILE_UPLOAD_PATH = "D:\\임용진\\5_spring_project\\memo\\memo_workspace\\images/";

    // input: MultipartFile, userLoginId(폴더 이름때문에 필요 aaaa(사용자이름)_13251513213(시간을 long형으로 변환) 
    // output: String(이미지 경로)
    public String uploadFile(MultipartFile file, String loginId) {
        // 폴더(디렉토리) 생성
        String directoryName = loginId + "_" + System.currentTimeMillis();
        String filePath = FILE_UPLOAD_PATH + directoryName + "/"; 

        // 폴더 생성
        File directory = new File(filePath);
        if (!directory.mkdir()) {
            return null; // 폴더 생성 실패 시 null 리턴(에러가 아님)
        }

        try {
            // 파일을 경로에 저장
            Path path = Paths.get(filePath + file.getOriginalFilename());
            Files.write(path, file.getBytes());

            // 파일 저장 성공 시 이미지 URL 반환
            return "/images/" + directoryName + "/" + file.getOriginalFilename();

        } catch (IOException e) {
            e.printStackTrace();
            return null; // 이미지 업로드 실패 시 null 리턴
        }
    }

    // 업로드된 이미지를 컴퓨터에서 삭제
    // input : imagePath
    // output : X
    public void deleteFile(String imagePath) {
        // \images/ 가 겹치기 때문에 제거해야 한다. 
        Path path = Paths.get(FILE_UPLOAD_PATH + imagePath.replace("/images/", ""));
        
        // 삭제할 이미지가 존재하는가?
        if (Files.exists(path)) {
            try {
                // 이미지 파일 삭제
                Files.delete(path);
            } catch (IOException e) {
                log.info("[파일매니저 파일삭제] imagePath:{}", imagePath);
                return;
            }

            // 폴더(디렉토리) 삭제
            path = path.getParent();
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.info("[파일매니저 폴더삭제] imagePath:{}", imagePath);
                }
            }
        }
    }
}
