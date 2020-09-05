package com.mahendracandi.chatbotgeneratereportapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface IMainService {
	boolean processFallbackMessage(String inputFile, String outputFile, String titleName, String sheetName);
	
	byte[] getFile(String fileName);
	
	String uploadFile(MultipartFile file, String targetPath);
	
	String createSourcePath(String originalFileName);
	
	String createOutputPath(String fileName);
}
