package com.mahendracandi.chatbotgeneratereportapp.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface IFallbackService {

    /**
     * Get Fallback Activity to Excel
     * @param workbook
     * @param fileInput
     */
    void getFallBackActivity(XSSFWorkbook workbook, String fileInput, String sheetName, String titleName);
}
