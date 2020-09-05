package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahendracandi.chatbotgeneratereportapp.common.SupportLogic;
import com.mahendracandi.chatbotgeneratereportapp.model.FallbackActivity;
import com.mahendracandi.chatbotgeneratereportapp.service.IFallbackService;
import com.mahendracandi.chatbotgeneratereportapp.service.IService;

@Service(value = "iFallbackService")
public class FallbackServiceImpl implements IFallbackService {

	@Autowired
	IService service;

	@Autowired
	SupportLogic supportLogic;

	@Override
	public void getFallBackActivity(XSSFWorkbook workbook, String fileInput, String sheetName, String titleName) {

		List<FallbackActivity> fallbackSept2018 = service.getFallbackActivityFromJsonFile(fileInput);

		createSheetAndContent(workbook, fallbackSept2018, sheetName, titleName);

		/*
		 * List<FallbackActivity> fallbackSept2018 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2018\\Fallback-Sep-2018 - Copy.json"
		 * ); List<FallbackActivity> fallbackOkt2018 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2018\\Fallback-Okt-2018 - Copy.json"
		 * ); List<FallbackActivity> fallbackNov2018 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2018\\Fallback-Nov-2018 - Copy.json"
		 * ); List<FallbackActivity> fallbackDec2018 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2018\\Fallback-Dec-2018 - Copy.json"
		 * ); List<FallbackActivity> fallbackJan2019 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2019\\Fallback-Jan-2019 - Copy.json"
		 * ); List<FallbackActivity> fallbackFeb2019 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2019\\Fallback-Feb-2019 - Copy.json"
		 * ); List<FallbackActivity> fallbackMar2019 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2019\\Fallback-Mar-2019 - Copy.json"
		 * ); List<FallbackActivity> fallbackApr2019 = service.
		 * getFallbackActivityFromJsonFile("D:\\Abdu\\PROJECT\\CHATBOT PROJECT\\1. FIF FILE\\REPORT\\FallbackMessage\\2019\\Fallback-Apr-01-To-26-2019 - Copy.json"
		 * );
		 */

		/*
		 * List<List<FallbackActivity>> allListCollection = new ArrayList<>();
		 * allListCollection.add(fallbackSept2018);
		 * allListCollection.add(fallbackOkt2018);
		 * allListCollection.add(fallbackNov2018);
		 * allListCollection.add(fallbackDec2018);
		 * allListCollection.add(fallbackJan2019);
		 * allListCollection.add(fallbackFeb2019);
		 * allListCollection.add(fallbackMar2019);
		 * allListCollection.add(fallbackApr2019);
		 */

		/*
		 * String sheetName = ""; for (int i = 0; i < allListCollection.size(); i++) {
		 * sheetName = getSheetName(i); createSheetAndContent(workbook,
		 * allListCollection.get(i), sheetName, sheetName); }
		 */
	}

	private String getSheetName(int count) {
		String sheetName = "";
		if (count == 0) {
			sheetName = "September 2018";
		} else if (count == 1) {
			sheetName = "Oktober 2018";
		} else if (count == 2) {
			sheetName = "November 2018";
		} else if (count == 3) {
			sheetName = "Desember 2018";
		} else if (count == 4) {
			sheetName = "January 2019";
		} else if (count == 5) {
			sheetName = "Februari 2018";
		} else if (count == 6) {
			sheetName = "March 2018";
		} else if (count == 7) {
			sheetName = "April 2018";
		}
		return sheetName;
	}

	private void createSheetAndContent(XSSFWorkbook workbook, List<FallbackActivity> listFallbackActivities,
			String sheetName, String titleName) {
		Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);
		// CREATE DINAMYC VARIABLE
		XSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));

		XSSFRow judul = sheet.createRow(0);
		// CREATE DYNAMIC VARIABLE
		judul.createCell(0).setCellValue("Fallback Message " + titleName);
		judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
		short x = 40 * 20;
		judul.setHeight(x);

		XSSFRow tableHeader = sheet.createRow(3);
		tableHeader.createCell(0).setCellValue("Create Date UTC");
		tableHeader.createCell(1).setCellValue("Created Date WIB");
		tableHeader.createCell(2).setCellValue("Intent Type"); // type
		tableHeader.createCell(3).setCellValue("Channel Type");
		tableHeader.createCell(4).setCellValue("Fallback Intent"); // intent
		tableHeader.createCell(5).setCellValue("Message");
		tableHeader.createCell(6).setCellValue("Account Name");
		tableHeader.createCell(7).setCellValue("Account Id");

		int startRow = tableHeader.getRowNum() + 1;
		for (FallbackActivity fallback : listFallbackActivities) {
			// CREATE LOOP !!!
			XSSFRow contentRow = sheet.createRow(startRow);
			contentRow.createCell(0).setCellValue(fallback.getCreated_date());
			contentRow.createCell(1).setCellValue(supportLogic.convertUTCtoLocalTime(fallback.getCreated_date()));
			contentRow.createCell(2).setCellValue(fallback.getType());
			contentRow.createCell(3).setCellValue(fallback.getChannel_type());
			contentRow.createCell(4).setCellValue(fallback.getIntent());
			
			if (fallback.getMessage().length() > 32766 ) {
				contentRow.createCell(5).setCellValue(fallback.getMessage().substring(0, 32766));
			} else {
				contentRow.createCell(5).setCellValue(fallback.getMessage());
			}
			
			contentRow.createCell(6).setCellValue(fallback.getAccount_name());
			contentRow.createCell(7).setCellValue(fallback.getAccount_id());
			startRow++;
		}

		for (int i = 0; i < tableHeader.getPhysicalNumberOfCells(); i++) {
			tableHeader.getCell(i).setCellStyle(mapStyle.get(SupportLogic.BOLD_12_BOTTOM_BORDER));
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.setColumnWidth(4, 40 * 125);
		sheet.setColumnWidth(5, 40 * 256);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);

	}

}
