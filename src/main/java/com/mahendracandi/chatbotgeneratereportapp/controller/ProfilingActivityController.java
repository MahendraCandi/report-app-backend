package com.mahendracandi.chatbotgeneratereportapp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahendracandi.chatbotgeneratereportapp.service.IMainService;

@RestController
@CrossOrigin
@RequestMapping("/api/profiling-activity")
public class ProfilingActivityController {

private static final Logger log = LogManager.getLogger(ProfilingActivityController.class);
	
	@Autowired
	IMainService mainService;
	
	@PostMapping(value = "/report/generate" , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> generateFallbackReport(
				@RequestParam("file") MultipartFile file,
				@RequestParam("outputFileName") String outputFileName,
				@RequestParam("titleName") String titleName,
				@RequestParam("sheetName") String sheetName) {
		
		log.info("multipart file: {}", file.getOriginalFilename());
		log.info("outputFile: {}", outputFileName);
		log.info("titleName: {}", titleName);
		log.info("sheetName: {}", sheetName);
		
		// create directory path
		String targetPath = mainService.createSourcePath(file.getOriginalFilename());
		log.info("targetPath: {}", targetPath);
		
		// save file to storage
		mainService.uploadFile(file, targetPath);
		
		// process file to excel
		String outputFile = mainService.createOutputPath(outputFileName);
		log.info("outputFile: {}", outputFile);
		
		boolean success = mainService.processProfilingActivity(targetPath, outputFile, titleName, sheetName);
		if (!success) {
			throw new NullPointerException("Failed processing profiling activity");
		}
		
		ByteArrayResource resource = new ByteArrayResource(mainService.getFile(outputFile));
		
		// get excel file send as byte array
		return ResponseEntity.ok().header("Content-disposition", "attachment; filename="+ outputFileName).body(resource);
	}
}
