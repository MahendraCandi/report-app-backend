package com.mahendracandi.chatbotgeneratereportapp.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface IKnowledgeService {
    void generateFAQKnowledge(String fileInput, String fileOutput, XSSFWorkbook workbook);

    void generateDuplicate(boolean generateDuplicate);

    void generateDuplicateFAQ(String fileInput, String fileOutput, XSSFWorkbook workbook);
}
