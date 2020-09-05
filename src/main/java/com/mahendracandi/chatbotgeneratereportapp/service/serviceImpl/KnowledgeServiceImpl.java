package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahendracandi.chatbotgeneratereportapp.common.SupportLogic;
import com.mahendracandi.chatbotgeneratereportapp.model.Knowledge;
import com.mahendracandi.chatbotgeneratereportapp.service.IKnowledgeService;
import com.mahendracandi.chatbotgeneratereportapp.service.IService;

@Service("iKnowledgeService")
public class KnowledgeServiceImpl implements IKnowledgeService {

    @Autowired
    IService service;

    @Autowired
    SupportLogic supportLogic;

    private static final Logger log = LogManager.getLogger(KnowledgeServiceImpl.class);
	
    private boolean generateDuplicate;
    private int startRow = 0;
    private int no = 0;
    private int totalAnswerFalse = 0;
    private int totalAnswerTrue = 0;
    private int totalFAQKnowledge = 0;
    private int totalRowFalse = 0;
    private int totalRowTrue = 0;
    private XSSFSheet sheet;
    private Map<String, XSSFCellStyle> mapStyle;
    private XSSFWorkbook workbook;
    private XSSFRow tableHeader;
    private String sheetName;
    private String title;
    private List<Knowledge> knowledgeList;

    @Override
    public void generateDuplicate(boolean generateDuplicate) {
        this.generateDuplicate = generateDuplicate;
    }

    @Override
    public void generateFAQKnowledge(String fileInput, String fileOutput, XSSFWorkbook workbook) {
        generateListFromJson(fileInput);

        this.workbook = workbook;
        sheetName = "FAQ Knowledge";
        title = sheetName;
        generateRowData(knowledgeList);
        styleHeaderAndAutoColumn();
        generateDuplicateFAQ(fileInput, fileOutput, this.workbook);

        log.info("finish generate knowledge excel");
    }

    @Override
    public void generateDuplicateFAQ(String fileInput, String fileOutput, XSSFWorkbook workbook) {
        if(generateDuplicate){
            log.info("Generate duplicate FAQ on processing");

            sheetName = "Duplicate Knowledge";
            title = sheetName;

            List<Knowledge> onlyFalse = knowledgeList.stream().filter(p -> !p.isAnswer())
                    .collect(Collectors.toList());
            Map<String, List<Knowledge>> mapUnique = onlyFalse.stream()
                    .collect(Collectors.groupingBy(p -> Arrays.toString(p.getContent()).toLowerCase()));
            Set<String> duplicateId = mapUnique.values().stream()
                    .filter(p -> p.size() > 1)
                    .flatMap(Collection::stream)
                    .map(p -> p.getKnowledge_id())
                    .collect(Collectors.toSet());
            List<Knowledge> duplicateKnowledge = knowledgeList.stream()
                    .filter(p -> duplicateId.contains(p.getKnowledge_id()))
                    .sorted(Comparator.comparing(Knowledge::isAnswer).thenComparing(k -> Arrays.toString(k.getContent())))
                    .collect(Collectors.toList());

            generateRowData(duplicateKnowledge);

            styleHeaderAndAutoColumn();

            log.info("Generate duplicate has done");
        }else {
            log.info("Generate duplicate: " + generateDuplicate);
        }
    }

    private void generateRowData(List<Knowledge> knowledgeList){
        // CREATE EXCEL ------------------------------------------------------
        log.info("Start generate knowledge excel");
        mapStyle = supportLogic.getMapStyle(workbook);

        sheet = workbook.createSheet(sheetName);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));

        XSSFRow judul = sheet.createRow(0);
        judul.createCell(0).setCellValue(title);
        judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
        short x = 40 * 20;
        judul.setHeight(x);

        XSSFRow date = sheet.createRow(3);
        date.createCell(0).setCellValue("Generate per " + supportLogic.getDateNow());
        date.getCell(0).setCellStyle(mapStyle.get(SupportLogic.BOLD_13));

        tableHeader = sheet.createRow(4);
        tableHeader.createCell(0).setCellValue("No.");
        tableHeader.createCell(1).setCellValue("Knowledge Id");
        tableHeader.createCell(2).setCellValue("Title");
        tableHeader.createCell(3).setCellValue("Module");
        tableHeader.createCell(4).setCellValue("Answer");
        tableHeader.createCell(5).setCellValue("Content");
        tableHeader.createCell(6).setCellValue("Content_Str");
        tableHeader.createCell(7).setCellValue("Spell");
        tableHeader.createCell(8).setCellValue("Primary Terms");
        tableHeader.createCell(9).setCellValue("Secondary Terms");
        tableHeader.createCell(10).setCellValue("Trainer");
        tableHeader.createCell(11).setCellValue("Create Date");
        tableHeader.createCell(12).setCellValue("Created By");
        tableHeader.createCell(13).setCellValue("Modified Date");
        tableHeader.createCell(14).setCellValue("Modified By");

        startRow = tableHeader.getRowNum() + 1;
        no = 1;
        totalAnswerFalse = 0;
        totalAnswerTrue = 0;
        totalFAQKnowledge = 0;
        totalRowFalse = 0;
        totalRowTrue = 0;

        for (Knowledge knowledge : knowledgeList) {
            XSSFRow knowledgeData = sheet.createRow(startRow);
            knowledgeData.createCell(0).setCellValue(String.valueOf(no));
            knowledgeData.createCell(1).setCellValue(knowledge.getKnowledge_id());
            knowledgeData.createCell(2).setCellValue(knowledge.getTitle());
            knowledgeData.createCell(3).setCellValue(knowledge.getModule());
            knowledgeData.createCell(4).setCellValue(knowledge.isAnswer());
            knowledgeData.createCell(5).setCellValue(Arrays.toString(knowledge.getContent()));
            if(!knowledge.isAnswer()){
                totalAnswerFalse += (knowledge.getContent() != null) ? knowledge.getContent().length : 0;
                totalFAQKnowledge++;
                totalRowFalse++;
            }else{
                totalAnswerTrue += (knowledge.getContent() != null) ? knowledge.getContent().length : 0;
                totalRowTrue++;
            }
            knowledgeData.createCell(6).setCellValue(Arrays.toString(knowledge.getContent_str()));
            knowledgeData.createCell(7).setCellValue(Arrays.toString(knowledge.getSpell()));
            knowledgeData.createCell(8).setCellValue(Arrays.toString(knowledge.getPrimary_terms()));
            knowledgeData.createCell(9).setCellValue(Arrays.toString(knowledge.getSecondary_terms()));
            knowledgeData.createCell(10).setCellValue(knowledge.getTrainer());
            knowledgeData.createCell(11).setCellValue(knowledge.getCreated_date());
            knowledgeData.createCell(12).setCellValue(knowledge.getCreated_by());
            knowledgeData.createCell(13).setCellValue(knowledge.getModified_date());
            knowledgeData.createCell(14).setCellValue(knowledge.getModified_by());

            // set wrap text
            for(int i = 0; i < 15; i++){
                knowledgeData.getCell(i).setCellStyle(mapStyle.get(SupportLogic.TOP_ALIGN));
                if((i == 5) || (i == 6) || (i == 7) || (i == 8) || (i == 9)){
                    knowledgeData.getCell(i).setCellStyle(mapStyle.get(SupportLogic.WRAP_TEXT));
                }
            }

            startRow++;
            no++;

            log.info("No." + no + " Answer False: " + ((knowledge.getContent() != null) ? totalAnswerFalse  : null));
            log.info("No." + no + " Answer True: " + ((knowledge.getContent() != null) ? totalAnswerTrue  : null));
            log.info("No." + no + " Row False: " + totalRowFalse);
            log.info("No." + no + " Row True: " + totalRowTrue);
            log.info("No." + no + " FAQ Knowledge: " + totalFAQKnowledge);
        }

        log.info("======== SUMMARY =========");

        int summaryRow = startRow + 2;
        XSSFRow summary1 = sheet.createRow(summaryRow);
        summary1.createCell(0).setCellValue("Total Knowledge");
        summary1.createCell(1).setCellValue(totalFAQKnowledge);
        summaryRow++;

        XSSFRow summary1a = sheet.createRow(summaryRow);
        summary1a.createCell(0).setCellValue("Total Row False");
        summary1a.createCell(1).setCellValue(totalRowFalse);
        summaryRow++;

        XSSFRow summary1b = sheet.createRow(summaryRow);
        summary1b.createCell(0).setCellValue("Total Row True");
        summary1b.createCell(1).setCellValue(totalRowTrue);
        summaryRow++;

        XSSFRow summary2 = sheet.createRow(summaryRow);
        summary2.createCell(0).setCellValue("Total Questions");
        summary2.createCell(1).setCellValue(totalAnswerFalse);
        summaryRow++;

        XSSFRow summary3 = sheet.createRow(summaryRow);
        summary3.createCell(0).setCellValue("Total Bot Answer");
        summary3.createCell(1).setCellValue(totalAnswerTrue);
        summaryRow++;

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        DateTime dtMax = knowledgeList.stream().map(z -> dtf.parseDateTime(z.getCreated_date())).max(DateTime::compareTo).get();
        DateTime dtMin = knowledgeList.stream().map(z -> dtf.parseDateTime(z.getCreated_date())).min(DateTime::compareTo).get();
        log.info("Date Max: " + dtMax.toString("dd MMMM yyyy"));
        log.info("Date Min: " + dtMin.toString("dd MMMM yyyy"));

        XSSFRow summary4 = sheet.createRow(summaryRow);
        summary4.createCell(0).setCellValue("Min. Date ");
        summary4.createCell(1).setCellValue(dtMin.toString("dd MMMM yyyy"));
        summaryRow++;

        XSSFRow summary5 = sheet.createRow(summaryRow);
        summary5.createCell(0).setCellValue("Max. Date ");
        summary5.createCell(1).setCellValue(dtMax.toString("dd MMMM yyyy"));
    }

    private void generateListFromJson(String fileInput){
        knowledgeList = service.getListFromJsonFile(fileInput);
        knowledgeList = knowledgeList.stream().sorted(Comparator.comparing(Knowledge::getKnowledge_id))
                .collect(Collectors.toList());
    }

    private void styleHeaderAndAutoColumn(){
        for(int i = 0; i < tableHeader.getPhysicalNumberOfCells(); i++){
            tableHeader.getCell(i).setCellStyle(mapStyle.get(SupportLogic.BOLD_12));
        }

        // set auto column 1 2 3 9 10 11 12 13 14
        int a = tableHeader.getPhysicalNumberOfCells();
        for (int i = 0; i < a; i++) {
            if(i == 5 || i == 6 || i == 7 || i == 8 || i == 9){
                sheet.setColumnWidth(i, 40*256);

            }else{
                if(i == 0) continue;
                sheet.autoSizeColumn(i);
            }
        }
    }
}
