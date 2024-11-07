package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileManagerService {
    // 실제 업로드된 이미지가 저장될 서버 경로
    public static final String FILE_UPLOAD_PATH = "D:\\임용진\\5_spring_project\\memo\\memo_workspace\\images/";

    // input: MultipartFile, userLoginId
    // output: String(이미지 경로)
    public String uploadFile(MultipartFile file, String loginId) {
        // 폴더(디렉토리) 생성
        // 예: aaaa_17237482334/sun.png
        String directoryName = loginId + "_" + System.currentTimeMillis();
        String filePath = FILE_UPLOAD_PATH + directoryName + "/";

        // 폴더 생성
        File directory = new File(filePath);
        if (!directory.mkdir()) {
            return null; // 폴더 생성 실패 시 null 리턴
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
}
