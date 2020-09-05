package com.mahendracandi.chatbotgeneratereportapp.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface ITicketService {

    /**
     * Get ticket Summary
     *
     * @param workbook
     * @param fileInput
     * @throws Exception
     */
    void getTicketSummary(XSSFWorkbook workbook, String fileInput, String headerTitle, String dateTitle);

    /**
     * Get tiket only agent response
     *
     * @param workbook
     * @param fileInput
     * @param fileOutput
     * @param startRow
     * @param sheetName
     */
    void geTicketXSSFRespondAgentTicketParentOnly(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, String sheetName, String mapId);

    /**
     * Get tiket only agent response pivot style
     *
     * @param workbook
     * @param fileInput
     * @param fileOutput
     * @param startRow
     * @param sheetName
     */
    void geTicketXSSFRespondAgentOnlyNewPivot(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, String sheetName, String mapId);

    /**
     * Get all ticket
     *
     * @param fileInput
     * @param fileOutput
     * @param startRow
     * @param sheetName
     */
    void getTicketXSSFAllCategory(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, String sheetName, String mapId);

    /**
     * Get all tiket with all attribute
     *
     * @param fileInput
     * @param fileOutput
     * @param startRow
     * @param endRow
     * @param sheetName
     */
    void getTicketVersionXSSF(String fileInput, String fileOutput, int startRow, int endRow, String sheetName);

    /**
     * Get tiket only agent response (Deprecated)
     *
     * @param workbook
     * @param fileInput
     * @param fileOutput
     * @param startRow
     * @param endRow
     * @param sheetName
     */
    void geTicketXSSFRespondAgentOnly(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, int endRow, String sheetName);

}
