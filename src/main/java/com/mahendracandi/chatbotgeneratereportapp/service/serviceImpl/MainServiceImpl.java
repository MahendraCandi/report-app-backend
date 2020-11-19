package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahendracandi.chatbotgeneratereportapp.common.CommonStatic;
import com.mahendracandi.chatbotgeneratereportapp.service.IFallbackService;
import com.mahendracandi.chatbotgeneratereportapp.service.IMainService;
import com.mahendracandi.chatbotgeneratereportapp.service.IProfilingActivity;

@Service
public class MainServiceImpl implements IMainService {

	private static final Logger log = LogManager.getLogger(MainServiceImpl.class);

	@Autowired
	IFallbackService fallbackService;
	
	@Autowired
	IProfilingActivity profilingService;

	@Override
	public boolean processFallbackMessage(String inputFile, String outputFile, String titleName, String sheetName) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			fallbackService.getFallBackActivity(workbook, inputFile, sheetName, titleName);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
		} catch (Exception e) {
			log.error("Error: {}", e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean processProfilingActivity(String inputFile, String outputFile, String titleName, String sheetName) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			profilingService.getProfilingActivity(workbook, inputFile, sheetName, titleName);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
		} catch (Exception e) {
			log.error("Error: {}", e);
			return false;
		}
		return true;
	}

	@Override
	public byte[] getFile(String fileName) {
		//String filePath = CommonStatic.ROOT_PATH + File.separator + CommonStatic.REPORT_PATH + File.separator + fileName;
		File file = new File(fileName);

		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			log.error("getFile() Error processing file: {}", e);
		}
		return null;
	}

	@Override
	public String uploadFile(MultipartFile file, String targetPath) {
		Path path = Paths.get(targetPath);

		try {
			// Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			OutputStream os = Files.newOutputStream(path);
//			os.write(file.getBytes());
			FileUtils.writeByteArrayToFile(new File(targetPath), file.getBytes());
		} catch (IOException e) {
			log.error("Error uploadFile(): {}", e);
		}

		return null;
	}
	
	@Override
	public String createSourcePath(String originalFileName) {

		if (originalFileName == null) {
			throw new NullPointerException("createSourcePath() File Name is null");
		}
		
		LocalDate dateNow = LocalDate.now();
		String dateStr = dateNow.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
		
		originalFileName = dateStr + "-" + originalFileName;

		return CommonStatic.ROOT_PATH + File.separator + CommonStatic.REPORT_PATH + File.separator + CommonStatic.SOURCE_PATH 
				+ File.separator + dateStr + File.separator + originalFileName;
	}
	
	public String createOutputPath(String fileName) {

		if (fileName == null) {
			throw new NullPointerException("createSourcePath() File Name is null");
		}
		
		LocalDate dateNow = LocalDate.now();
		String dateStr = dateNow.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
		
		String path = CommonStatic.ROOT_PATH + File.separator + CommonStatic.REPORT_PATH + File.separator + CommonStatic.OUT_PATH 
				+ File.separator + dateStr;
		
		// create directory
		File file = new File(path);
		file.mkdirs();		
		
		return path + File.separator + fileName;
	}
}
