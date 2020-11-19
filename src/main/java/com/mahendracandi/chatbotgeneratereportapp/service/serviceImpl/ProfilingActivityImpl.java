package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.util.Collections;
import java.util.LinkedHashSet;
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
import com.mahendracandi.chatbotgeneratereportapp.model.FallbackActivity;
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
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 5));

		XSSFRow judul = sheet.createRow(0);
		// CREATE DYNAMIC VARIABLE
		judul.createCell(0).setCellValue("Profiling Activity " + titleName);
		judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
		short x = 40 * 20;
		judul.setHeight(x);

		XSSFRow tableHeader = sheet.createRow(3);
		tableHeader.createCell(0).setCellValue("No");
		tableHeader.createCell(1).setCellValue("Customer Id");
		tableHeader.createCell(2).setCellValue("Customer Name");
		tableHeader.createCell(3).setCellValue("Contract");
		tableHeader.createCell(4).setCellValue("KTP");
		tableHeader.createCell(5).setCellValue("Keterangan");

		Set<String> setAccountId = new LinkedHashSet<String>();		
		List<ProfilingActivity> distinctListByAccountId = listProfilingActivity.stream()
				.filter(p -> setAccountId.add(p.getAccount_id()))
				.collect(Collectors.toList());
		
		int startRow = tableHeader.getRowNum() + 1;
		int number = 1;
		for (ProfilingActivity profiling : distinctListByAccountId) {
			// CREATE LOOP !!!
			XSSFRow contentRow = sheet.createRow(startRow);
			contentRow.createCell(0).setCellValue(number);
			contentRow.createCell(1).setCellValue(profiling.getAccount_id());
			contentRow.createCell(2).setCellValue(profiling.getAccount_name());
			
			Set<String> contracts = listProfilingActivity.stream()
					.filter(p -> p.getAccount_id().equals(profiling.getAccount_id()) && 
							p.getEntity_name().equalsIgnoreCase("question_3") && 
							supportLogic.isContractNoValid(p.getEntity_value()))
					.map(ProfilingActivity::getEntity_value)
					.collect(Collectors.toSet());
			contentRow.createCell(3).setCellValue(String.join(",", contracts));
			
			Set<String> ktp = listProfilingActivity.stream()
					.filter(p -> p.getAccount_id().equals(profiling.getAccount_id()) && 
							p.getEntity_name().equalsIgnoreCase("question_2") &&
							supportLogic.isIdentityNoValid(p.getEntity_value()))
					.map(ProfilingActivity::getEntity_value)
					.collect(Collectors.toSet());
			contentRow.createCell(4).setCellValue(String.join(",", ktp));
			
			Set<String> keterangan = listProfilingActivity.stream()
					.filter(p -> p.getAccount_id().equals(profiling.getAccount_id()) && p.getEntity_name().equalsIgnoreCase("kendala"))
					.map(p -> {
						if (p.getEntity_value().equalsIgnoreCase("1")) {
							return "Penagihan kontrak orang lain";
						} else if (p.getEntity_value().equalsIgnoreCase("2")) {
							return "Cek nomor kontrak";
						} else if (p.getEntity_value().equalsIgnoreCase("3")) {
							return "Sambungkan ke CS";
						}  else if (p.getEntity_value().equalsIgnoreCase("batal")) {
							return "Customer ketik batal";
						}else {
							return "Customer random input";
						}
					})
					.collect(Collectors.toSet());
			
			String keteranganStr = "";
			if (contracts.isEmpty() && ktp.isEmpty()) {
				keteranganStr = keterangan.isEmpty() ? "Input customer tidak ditemukan" : String.join(", ", keterangan);
			}
			contentRow.createCell(5).setCellValue(keteranganStr);
			
			startRow++;
			number++;
		}

		for (int i = 0; i < tableHeader.getPhysicalNumberOfCells(); i++) {
			tableHeader.getCell(i).setCellStyle(mapStyle.get(SupportLogic.BOLD_12_BOTTOM_BORDER));
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.setColumnWidth(2, 40 * 125);
		sheet.setColumnWidth(3, 40 * 125);
		sheet.setColumnWidth(4, 40 * 125);
		sheet.autoSizeColumn(5);

	}
}
