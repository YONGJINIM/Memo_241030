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
    // 경로 마지막에 슬래시(/)를 붙여준다 없으면 경로를 제대로 인식하지 못한다. 
    public static final String FILE_UPLOAD_PATH = "D:\\임용진\\5_spring_project\\memo\\memo_workspace\\images/";

 // input: MultipartFile, userLoginId(폴더 이름 때문에 필요, 예: aaaa(사용자이름)_13251513213(시간을 long형으로 변환) 
 // output: String(이미지 경로)
 public String uploadFile(MultipartFile file, String loginId) {
     // 폴더(디렉토리) 생성
     // directoryName은 "aaaa(loginId)_12341222132132(현재 시간의 long 타입 값)"
     // 사용자의 ID와 현재 시간을 조합하여 고유 폴더 이름을 만듭니다. 예: "user123_17237482334"
     String directoryName = loginId + "_" + System.currentTimeMillis(); // 브레이크 포인트 걸어서 확인
     String filePath = FILE_UPLOAD_PATH + directoryName + "/"; 

     // 폴더 생성
     // File 객체 생성: 업로드할 경로 + 고유한 폴더 이름을 사용하여 File 객체 생성
     // mkdir(): 폴더 생성. 이미 존재하는 경우 false를 반환하여 이후에 null을 반환해 오류를 방지
     File directory = new File(filePath);
     if (!directory.mkdir()) {
         return null; // 폴더 생성 실패 시 null 리턴(에러가 아님)
     }

     // 파일 업로드
     try {
         // 경로에 파일 저장
         // 한글 파일명은 업로드가 불가하므로 나중에 파일명을 영문으로 변경할 예정
         
         // Path 객체 생성: filePath (생성된 디렉토리) + file의 원래 파일명으로 Path 객체 생성
         // 예: "D:/path_to_images/user123_17237482334/sun.png"
         Path path = Paths.get(filePath + file.getOriginalFilename()); // java.nio.file 패키지에서 Path 클래스 사용

         // Files.write(): 파일을 byte 배열로 받아 지정된 경로에 저장
         // MultipartFile의 getBytes() 메서드를 사용해 파일을 byte 배열로 변환한 후, Files.write()로 해당 위치에 기록합니다.
         Files.write(path, file.getBytes()); // path에 byte로 변환된 파일을 넘긴다.

         // 파일 저장 성공 시 이미지 URL 반환
         // /images 디렉토리와 파일 경로(directoryName + file 이름)를 조합하여 이미지 URL을 반환
         // 주소는 이렇게 될 것이다. (예상)
         // 예: /images/user123_17237482334/sun.png
         return "/images/" + directoryName + "/" + file.getOriginalFilename();

     } catch (IOException e) {
         e.printStackTrace();
         return null; // 이미지 업로드 실패 시 경로를 null 리턴한다. (에러 아님)
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
