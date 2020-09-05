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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahendracandi.chatbotgeneratereportapp.common.SupportLogic;
import com.mahendracandi.chatbotgeneratereportapp.model.Knowledge;
import com.mahendracandi.chatbotgeneratereportapp.service.IDuplicateKnowledge;
import com.mahendracandi.chatbotgeneratereportapp.service.IService;

@Service("iDuplicateKnowledge")
public class DuplicateKnowledge implements IDuplicateKnowledge {

	private static final Logger log = LogManager.getLogger(DuplicateKnowledge.class);

    @Autowired
    IService service;

    @Autowired
    SupportLogic supportLogic;

    @Override
    public void generateDuplicateKnowledge(String fileInput, String fileOutput, XSSFWorkbook workbook) {
        List<Knowledge> knowledgeList = service.getListFromJsonFile(fileInput);
        knowledgeList = knowledgeList.stream().sorted(Comparator.comparing(Knowledge::getKnowledge_id))
                .collect(Collectors.toList());

        /*Set<Knowledge> duplicate = new HashSet<>();
        List<Knowledge> onlyFalse = knowledgeList.stream().filter(p -> !p.isAnswer())
                .collect(Collectors.toList());
        for (int index = 0; index < onlyFalse.size(); index++) {
            String content1 = Arrays.toString(onlyFalse.get(index).getContent());
            String id1 = onlyFalse.get(index).getKnowledge_id();
            log.debug("CONTENT 1: " + id1 + " " + content1);
            for (int index2 = 0; index2 < onlyFalse.size(); index2++) {
                String content2 = Arrays.toString(onlyFalse.get(index2).getContent());
                String id2 = onlyFalse.get(index2).getKnowledge_id();
                if (content2.equalsIgnoreCase(content1)) {
                    if (!id2.equalsIgnoreCase(id1)) {
                        log.error("BOOM " + id2 + ": " + content2);
                        duplicate.add(onlyFalse.get(index));
                    }
                }
            }
        }
                Set<String> diff = duplicate.stream()
                .filter(p -> !duplicateKnowledge.contains(p))
                .map(p -> p.getKnowledge_id())
                .collect(Collectors.toSet());
        diff.stream().forEach(p -> System.out.println("DIFF: " + p));

        */

//        log.info("Knowledge List Size: " + knowledgeList.size());
//        log.info("Duplicate List Size: " + duplicate.size());
//        log.info("Difference size: " + differenceId.size());
//        log.info("Content size: " + content.size());

        //        differenceId.stream().forEach(p -> log.info("Difference: " + p));
//        content.stream().forEach(p -> log.info("content: " + p));
//        onlyFalse.stream().filter(p -> Collections.frequency(onlyFalse, Arrays.toString(p.getContent())) > 1)
//                .collect(Collectors.toSet()).forEach(p -> System.out.println(p.getKnowledge_id()));
//        log.info("Duplicate  stream lambda List Size: " + duplicate.size());
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
                .collect(Collectors.toList());

        // CREATE EXCEL ------------------------------------------------------
        /*log.info("Start generate knowledge excel");
        Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);

        XSSFSheet sheet = workbook.createSheet("FAQ Knowledge");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));

        XSSFRow judul = sheet.createRow(0);
        judul.createCell(0).setCellValue("FAQ Knowledge");
        judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
        short x = 40 * 20;
        judul.setHeight(x);

        XSSFRow date = sheet.createRow(3);
        date.createCell(0).setCellValue("Generate per " + supportLogic.getDateNow());
        date.getCell(0).setCellStyle(mapStyle.get(SupportLogic.BOLD_13));

        XSSFRow tableHeader = sheet.createRow(4);
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

        int startRow = tableHeader.getRowNum() + 1;
        int no = 1;
        int totalAnswerFalse = 0;
        int totalAnswerTrue = 0;
        int totalFAQKnowledge = 0;
        int totalRowFalse = 0;
        int totalRowTrue = 0;

        for (Knowledge knowledge : duplicateKnowledge) {
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

        log.info("Duplicate Id List Size: " + duplicateId.size());
        log.info("Duplicate knowledge List Size: " + duplicateKnowledge.size());
        log.info("Duplicate knowledge List false size: " + duplicateKnowledge.stream().filter(p -> !p.isAnswer()).count());*/


    }
}
