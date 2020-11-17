package com.mahendracandi.chatbotgeneratereportapp.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface IProfilingActivity {
	
	/**
	 * 
	 * 
	 */
	void getProfilingActivity(XSSFWorkbook workbook, String fileInput, String sheetName, String titleName);
}
