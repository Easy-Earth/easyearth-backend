package com.kh.spring.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
	
	@Value("${file.upload.path}")
	private String savePath;
	
	public String saveFile(MultipartFile uploadFile) throws Exception {
		
		//원본 파일명
		String originName = uploadFile.getOriginalFilename();
		
		//시간형식
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		//랜덤값 5자리 추출
		int ranNum = (int)(Math.random()*90000+10000);
		
		//확장자 추출
		String ext = originName.substring(originName.lastIndexOf("."));
		
		//합쳐주기
		String changeName =  currentTime + ranNum + ext;
		
		//서버에 업로드 처리
		uploadFile.transferTo(new File(savePath + changeName));
		
		return changeName;
	}

	//첨부파일 삭제
	public boolean deleteFile(String deleteFile) {
		
		//저장경로 + 저장파일명 
		String filePath = savePath + deleteFile;
		
		File file = new File(filePath);
		
		if(file.exists()) {
			
			return file.delete();
		}
		
		return false;
	}

}
