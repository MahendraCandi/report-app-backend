package com.mahendracandi.chatbotgeneratereportapp.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface IDuplicateKnowledge {

    void generateDuplicateKnowledge(String fileInput, String fileOutput, XSSFWorkbook workbook);

}
