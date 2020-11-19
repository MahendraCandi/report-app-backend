package com.mahendracandi.chatbotgeneratereportapp.common;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;

@Component
public class SupportLogic {
	private static final Logger log = LogManager.getLogger(SupportLogic.class);

    public final static String HEADER_STYLE = "header_style";
    public final static String BOLD_13 = "bold_13";
    public final static String BOLD_12 = "bold_12";
    public final static String BOLD_11 = "bold_11";
    public final static String BOLD_11_TOP_BORDER = "bold_11_top_border";
    public final static String BOLD_12_BOTTOM_BORDER = "bold_12_bottom_border";
    public final static String WRAP_TEXT = "wrap_text";
    public final static String TOP_ALIGN = "top_align";

    /**
     * Remove html tag
     *
     * @param htmlText
     * @return
     */
    public String removeHtmlTag(String htmlText) {
        if(htmlText == null || htmlText.isEmpty()) return "-";

        Source source = new Source(htmlText);
        Renderer renderer = source.getRenderer();
        String newline = System.getProperty("line.separator");
        String newText = renderer.toString().replace(newline, "\n");
        return newText.replace("\n\n", "\n");
    }

    /**
     * Generate from second (long type) to hours, minutes and seconds
     *
     * @param second
     * @return
     */
    public String getHourMinuteSecond(long second) {
        long minutesInMilli = 60;
        long hoursInMilli = 3600;

        long hours = second / hoursInMilli;
        second = second % hoursInMilli;

        long minutes = second / minutesInMilli;
        second = second % minutesInMilli;

        long seconds = second;

        StringBuilder builder = new StringBuilder()
                .append(String.valueOf(hours) + "hr ")
                .append(String.valueOf(minutes) + "min ")
                .append(String.valueOf(seconds) + "s");
        return builder.toString();
    }

    /**
     * return true if match {button: xxx }
     *
     * @param message
     * @return
     */
    public boolean getButtonMessage(String message){
        if(message == null) return false;
        String regex = "(\\{button:)(.+?)(}$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    /**
     * get ButtonMessage Model
     *
     * @param message
     * @return
     */
    public String replaceMessageToButton(String message){

        String regex = "^(\\{button:)(.+?)(]})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        String button = matcher.group(2).replace("\'", "\"") + "]";
        String button2 = button.replace("\"{", "{").replace("}\"", "}");
        return button2;
    }

    public static <T> Predicate<T> disticntByKey(Function<? super T, ?> ke){
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(ke, Boolean.TRUE) == null;
    }

    /**
     * Create style to excel cell
     *
     * @param workbook
     * @return
     */
    public Map<String, XSSFCellStyle> getMapStyle(XSSFWorkbook workbook) {
        Map<String, XSSFCellStyle> cellStyles = new HashMap<String, XSSFCellStyle>();
        XSSFCellStyle cellStyle;
        XSSFFont fontStyle;

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();

        //byte[] rgb = new byte[]{(byte) 146, (byte) 208, (byte) 80};
        //cellStyle.setFillForegroundColor(new XSSFColor(rgb, null));
        //cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        fontStyle.setFontHeightInPoints(((short) 24));
        fontStyle.setFontName("Calibri");
        fontStyle.setBold(true);
        cellStyle.setFont(fontStyle);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyles.put(HEADER_STYLE, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 13));
        fontStyle.setFontName("Calibri");
        fontStyle.setBold(true);
        cellStyle.setFont(fontStyle);
        cellStyles.put(BOLD_13, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 12));
        fontStyle.setFontName("Calibri");
        fontStyle.setBold(true);
        cellStyle.setFont(fontStyle);
        cellStyles.put(BOLD_12, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 12));
        fontStyle.setFontName("Calibri");
        fontStyle.setBold(true);
        cellStyle.setFont(fontStyle);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.put(BOLD_12_BOTTOM_BORDER, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 11));
        fontStyle.setFontName("Calibri");
        fontStyle.setBold(true);
        cellStyle.setFont(fontStyle);
        cellStyles.put(BOLD_11, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 11));
        fontStyle.setFontName("Calibri");
        fontStyle.setBold(true);
        cellStyle.setFont(fontStyle);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyles.put(BOLD_11_TOP_BORDER, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 11));
        fontStyle.setFontName("Calibri");
        cellStyle.setFont(fontStyle);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);
        cellStyles.put(WRAP_TEXT, cellStyle);

        cellStyle = workbook.createCellStyle();
        fontStyle = workbook.createFont();
        fontStyle.setFontHeightInPoints(((short) 11));
        fontStyle.setFontName("Calibri");
        cellStyle.setFont(fontStyle);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyles.put(TOP_ALIGN, cellStyle);

        return cellStyles;
    }

    /**
     * Return date format "dd MMMM yyyy"
     * @return
     */
    public String getDateNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"));
        return sdf.format(new Date());
    }
    
    /**
     * convert UTC to local date time. ex: 2020-06-30T17:03:36.481Z to 01-07-2020 00:03:36
     */
    public String convertUTCtoLocalTime(String utcDateTime) {
    	Instant instant = Instant.parse(utcDateTime);
		ZonedDateTime zoneDateTime = instant.atZone(ZoneId.systemDefault());
		return zoneDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
    
    /**
     * True if contract number has 12 digit
     * @param contractNumber
     * @return boolean
     */
    public boolean isContractNoValid(String contractNumber) {
        String regex = "^\\d{12}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(contractNumber);
        return m.find();
    }
    
    /**
     * True if identity number has 16 digit
     * @param identityNumber
     * @return boolean
     */
    public boolean isIdentityNoValid(String identityNumber) {
        String regex = "^\\d{16}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(identityNumber);
        return m.find();
    }
}
