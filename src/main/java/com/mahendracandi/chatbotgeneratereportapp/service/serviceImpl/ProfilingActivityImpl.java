package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahendracandi.chatbotgeneratereportapp.common.SupportLogic;
import com.mahendracandi.chatbotgeneratereportapp.model.ProfilingActivity;
import com.mahendracandi.chatbotgeneratereportapp.service.IProfilingActivity;
import com.mahendracandi.chatbotgeneratereportapp.service.IService;

@Service
public class ProfilingActivityImpl implements IProfilingActivity{

	@Autowired
	IService service;

	@Autowired
	SupportLogic supportLogic;
	
	@Override
	public void getProfilingActivity(XSSFWorkbook workbook, String fileInput, String sheetName, String titleName) {
		List<ProfilingActivity> list = service.getProfilingActivityFromJsonFile(fileInput);
		createSheetAndContent(workbook, list, sheetName, titleName);
	}
	
	private void createSheetAndContent(XSSFWorkbook workbook, List<ProfilingActivity> listProfilingActivity,
			String sheetName, String titleName) {
		Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);
		// CREATE DINAMYC VARIABLE
		XSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 6));

		XSSFRow judul = sheet.createRow(0);
		// CREATE DYNAMIC VARIABLE
		judul.createCell(0).setCellValue(titleName);
		judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
		short x = 40 * 20;
		judul.setHeight(x);

		XSSFRow tableHeader = sheet.createRow(3);
		tableHeader.createCell(0).setCellValue("No");
		tableHeader.createCell(1).setCellValue("Tanggal Akses");
		tableHeader.createCell(2).setCellValue("Channel");
		tableHeader.createCell(3).setCellValue("Ticket Id");
		tableHeader.createCell(4).setCellValue("No Hp");
		tableHeader.createCell(5).setCellValue("NIK");
		tableHeader.createCell(6).setCellValue("Kontrak");

		
		Set<String> setTicketNumber = new LinkedHashSet<String>();		
		List<ProfilingActivity> distinctListByTicketNumber = listProfilingActivity.stream()
				.filter(p -> setTicketNumber.add(p.getTicket_number()))
				.collect(Collectors.toList());
		
		int startRow = tableHeader.getRowNum() + 1;
		int number = 1;
		for (ProfilingActivity profiling : distinctListByTicketNumber) {
			
			List<ProfilingActivity> containsContactAndKtp = listProfilingActivity.stream()
					.filter(p -> p.getTicket_number().equals(profiling.getTicket_number()))
					.filter(p -> p.getEntity_name().equalsIgnoreCase("question_3") && supportLogic.isContractNoValid(p.getEntity_value()) || 
							p.getEntity_name().equalsIgnoreCase("question_2") && supportLogic.isIdentityNoValid(p.getEntity_value()))
					.collect(Collectors.toCollection(LinkedList::new));
			
			Set<String> contracts = containsContactAndKtp.stream()
					.filter(p -> p.getEntity_name().equalsIgnoreCase("question_3") && 
							supportLogic.isContractNoValid(p.getEntity_value()))
					.map(ProfilingActivity::getEntity_value)
					.collect(Collectors.toCollection(LinkedHashSet::new));
			
			Set<String> ktp = containsContactAndKtp.stream()
					.filter(p -> p.getEntity_name().equalsIgnoreCase("question_2") &&
							supportLogic.isIdentityNoValid(p.getEntity_value()))
					.map(ProfilingActivity::getEntity_value)
					.collect(Collectors.toCollection(LinkedHashSet::new));
			
			if (contracts.isEmpty() && ktp.isEmpty()) continue; 

			String lastDate = supportLogic.convertUTCtoLocalTime(
					containsContactAndKtp.get(containsContactAndKtp.size() - 1).getCreated_date());
			
			XSSFRow contentRow = sheet.createRow(startRow);
			contentRow.createCell(0).setCellValue(number);
			contentRow.createCell(1).setCellValue(lastDate);
			contentRow.createCell(2).setCellValue(profiling.getChannel_type());
			contentRow.createCell(3).setCellValue(profiling.getTicket_number());
			contentRow.createCell(4).setCellValue(profiling.getAccount_id());
			contentRow.createCell(5).setCellValue(String.join(",", ktp));
			contentRow.createCell(6).setCellValue(String.join(",", contracts));
			
			startRow++;
			number++;
		}
		
		for (int i = 0; i < tableHeader.getPhysicalNumberOfCells(); i++) {
			tableHeader.getCell(i).setCellStyle(mapStyle.get(SupportLogic.BOLD_12_BOTTOM_BORDER));
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);

	}
}
