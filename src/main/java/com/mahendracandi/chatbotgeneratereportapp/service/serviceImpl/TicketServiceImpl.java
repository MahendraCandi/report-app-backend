package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mahendracandi.chatbotgeneratereportapp.common.SupportLogic;
import com.mahendracandi.chatbotgeneratereportapp.model.ButtonMessage;
import com.mahendracandi.chatbotgeneratereportapp.model.ButtonSuggestion;
import com.mahendracandi.chatbotgeneratereportapp.model.ButtonValues;
import com.mahendracandi.chatbotgeneratereportapp.model.Ticket;
import com.mahendracandi.chatbotgeneratereportapp.service.IService;
import com.mahendracandi.chatbotgeneratereportapp.service.ITicketService;

@Service(value = "iTicketService")
public class TicketServiceImpl implements ITicketService {

    @Autowired
    IService service;

    @Autowired
    SupportLogic supportLogic;

    private static Map<String, String> mapAcc = new HashMap<>();
    private static Map<String, String> mapFIF = new HashMap<>();

    private final static Integer maximumLength = 30000;
    private final static String HEADER_STYLE = "header_style";
    private final static String BOLD_13 = "bold_13";
    private final static String BOLD_12 = "bold_12";
    private final static String BOLD_11 = "bold_11";
    private final static String BOLD_11_TOP_BORDER = "bold_11_top_border";

    private static final Logger log = LogManager.getLogger(TicketServiceImpl.class);

    static {
        mapAcc.put("c064d14b9b6131cdb69132871f249afa", "Agent 01");
        mapAcc.put("fcc7a2f090a790ee1774c7c4eb5f263f", "SPV 01");
        mapAcc.put("ce7b68b2c105c40eac40517b8105cbff", "YUNA");
        mapAcc.put("1626679147", "YUNA LINE");
        mapAcc.put("d33a884204b7bedd097f2c94e01d9a50", "ICN Supervisor");
        mapAcc.put("e90d515642956ecc3110ff56d1fe21f9", "ACC AGENT");

        mapFIF.put("623b6954a3f6b7003e41837cbcca7382", "Mimi Agent3");
        mapFIF.put("9b51b7f24583c4805d2c8b4b0e1c6ce3", "Eka Nuriati");
        mapFIF.put("d05b12e487994950a21327d7b2b1d98e", "Winagent");
        mapFIF.put("fd45b628b129801ae61d395a4a6cefc2", "FIONA");
        mapFIF.put("e0e411622ffefdc607055a28b28fc5eb", "Onny Amanda");
        mapFIF.put("81e2deb003f35f80dc85b49098fa9229", "Vania Tertia");
        mapFIF.put("ffde85891b052cbeeb87a9dd27db8c6a", "Gabriel Supervisor");
        mapFIF.put("0d5dc95bddc6dc0bf021b113aac1019a", "Winsupervisor");
        mapFIF.put("5f088eaec6def96cdefe2620833ea2f3", "Training Supervisor");
        mapFIF.put("eb31241bef550890a4dae1e4f236ca32", "Candi Supervisor");
        mapFIF.put("777ae0b9dec3ab9109ec45b575fdd238", "Cindy Aleyana");
        mapFIF.put("8e18eba78bb4feb4360d970135c30a86", "Eufrasia Mcpramulia");
        mapFIF.put("178bcb3e8f6cd3060bcde3f7593ed291", "Halofifagent1");

    }

    @Override
    public void getTicketSummary(XSSFWorkbook workbook, String fileInput, String headerTitle, String dateTitle) {
        List<Ticket> list = service.getTicketListFromJsonFile(fileInput);

        // All Ticket sorting by ticket number then created date --------------------------------------------
        List<Ticket> listTicketDto = list.stream()
                .sorted(Comparator.comparing(Ticket::getTicket_number).thenComparing(Ticket::getCreated_date))
                .collect(Collectors.toList());
        log.info("Total Row: " + listTicketDto.size());

        // Total ticket
        List<Ticket> totalTicket = listTicketDto.stream()
                .filter(p -> p.getParent().equalsIgnoreCase("1") && p.getRedistribute().equalsIgnoreCase("false"))
                .collect(Collectors.toList());
        log.info("Total Ticket: " + totalTicket.size());

        // Total ticket to agent
        Set<String> totalTicketToAgent = getTicketToAgent(totalTicket);
        log.info("Total Ticket To Agent: " + totalTicketToAgent.size());

        // Total ticket respond by agent
        List<Ticket> totalTicketRespondByAgent = getParentTicketRespondByAgent(listTicketDto);
        log.info("Total Ticket Respond By Agent: " + totalTicketRespondByAgent.size());

        // Total Ticket unrespond by agent
        Set<String> totalTicketClosedUnrespondAgent = getTicketClosedUnrespondByAgent(totalTicketToAgent, totalTicketRespondByAgent);
        log.info("Ticket Unrespond By Agent: " + totalTicketClosedUnrespondAgent.size());

        // Ticket responded SLA
        long ticketSLA = countTicketsRespondWithInSLA(totalTicketRespondByAgent);
        log.info("Total Ticket Responded SLA: " + ticketSLA);

        // Ticket not responded SLA
        long ticketNotSLA = countNotTicketsRespondWithInSLA(totalTicketRespondByAgent);
        log.info("Total Ticket Responded NOT SLA: " + ticketNotSLA);

        // max respond agent
        String maxRespondAgent = getMaxRespondAgent(totalTicketRespondByAgent);
        log.info("Max Respond: " + maxRespondAgent);

        // min respond agent
        String minRespondAgent= getMinRespondAgent(totalTicketRespondByAgent);
        log.info("Min Respond: " + minRespondAgent);

        // average respond agent
        String averageRespondAgent = getAverageRespondAgent(totalTicketRespondByAgent);
        log.info("Average Respond: " + averageRespondAgent);

        // webchat -----------------------------------------------------------------------------------------
        List<Ticket> totalWebchatTicket = getListPerchannelType(totalTicket, "webchat");
        log.info("Total Webchat Ticket: " + totalWebchatTicket.size());

        // webchat total responded agent
        List<Ticket> totalWebchatTicketRespondedByAgentParent = getTicketRespondPerchannel(totalTicketRespondByAgent, "webchat");
        log.info("Total Webchat Ticket Responded by agent: " + totalWebchatTicketRespondedByAgentParent.size());

        // webchat responded SLA
        long totalWebchatWithInSLA = countTicketsRespondWithInSLA(totalWebchatTicketRespondedByAgentParent);
        log.info("Total Webchat Ticket Responded SLA: " + totalWebchatWithInSLA);

        // webchat not responded SLA
        long totalWebchatNotWithInSLA = countNotTicketsRespondWithInSLA(totalWebchatTicketRespondedByAgentParent);
        log.info("Total Webchat Ticket Responded NOT SLA: " + totalWebchatNotWithInSLA);

        // max respond webchat
        String maxRespondWebchat = getMaxRespondAgent(totalWebchatTicketRespondedByAgentParent);
        log.info("Max Respond: " + maxRespondWebchat);

        // min respond webchat
        String minRespondWebchat= getMinRespondAgent(totalWebchatTicketRespondedByAgentParent);
        log.info("Min Respond: " + minRespondWebchat);

        // average respond webchat
        String averageRespondWebchat = getAverageRespondAgent(totalWebchatTicketRespondedByAgentParent);
        log.info("Average Respond: " + averageRespondWebchat);

        // line -------------------------------------------------------------------------------------------
        List<Ticket> totalLineTicket = getListPerchannelType(totalTicket, "linebot");
        log.info("Total Line Ticket: " + totalLineTicket.size());

        // line total respond agent
        List<Ticket> totalLineTicketRespondedByAgentParent = getTicketRespondPerchannel(totalTicketRespondByAgent, "linebot");
        log.info("Total Line Ticket Responded by agent: " + totalLineTicketRespondedByAgentParent.size());

        // line responded SLA
        long totalLineWithInSLA = countTicketsRespondWithInSLA(totalLineTicketRespondedByAgentParent);
        log.info("Total line Ticket Responded SLA: " + totalLineWithInSLA);

        // line not respond SLA
        long totalLineNotWithInSLA = countNotTicketsRespondWithInSLA(totalLineTicketRespondedByAgentParent);
        log.info("Total line Ticket Responded NOT SLA: " + totalLineNotWithInSLA);

        // max respond line
        String maxRespondLine = getMaxRespondAgent(totalLineTicketRespondedByAgentParent);
        log.info("Max Respond: " + maxRespondLine);

        // min respond line
        String minRespondLine= getMinRespondAgent(totalLineTicketRespondedByAgentParent);
        log.info("Min Respond: " + minRespondLine);

        // average respond line
        String averageRespondLine = getAverageRespondAgent(totalLineTicketRespondedByAgentParent);
        log.info("Average Respond: " + averageRespondLine);

        // faceboook ---------------------------------------------------------------------------------------
        List<Ticket> totalFacebookChat = getListPerchannelType(totalTicket, "facebook");
        log.info("Total Facebook Chat: " + totalFacebookChat.size());

        // facebook chat total respond agent
        List<Ticket> totalFacebookChatRespondedByAgent = getTicketRespondPerchannel(totalTicketRespondByAgent, "facebook");
        log.info("Total Facebook chat Respond by Agent: " + totalFacebookChatRespondedByAgent.size());

        // facebook chat respond SLA
        long totalFacebookWithInSLA = countTicketsRespondWithInSLA(totalFacebookChatRespondedByAgent);
        log.info("Facebook SLA: " + totalFacebookWithInSLA);

        // facebook chat respond not SLA
        long totalFacebookNotWithInSLA = countNotTicketsRespondWithInSLA(totalFacebookChatRespondedByAgent);
        log.info("Facebook SLA: " + totalFacebookNotWithInSLA);
        
        // max respond facebook
        String maxRespondFacebook = getMaxRespondAgent(totalFacebookChatRespondedByAgent);
        log.info("Max Respond: " + maxRespondFacebook);

        // min respond facebook
        String minRespondFacebook= getMinRespondAgent(totalFacebookChatRespondedByAgent);
        log.info("Min Respond: " + minRespondFacebook);

        // average respond facebook
        String averageRespondFacebook = getAverageRespondAgent(totalFacebookChatRespondedByAgent);
        log.info("Average Respond: " + averageRespondFacebook);
        
        // Button ---------------------------------------------------------------------------

        List<String> messagesHasButton = listTicketDto.stream()
                .filter(p -> supportLogic.getButtonMessage(p.getMessage()))
                .map(Ticket::getMessage)
                .collect(Collectors.toList());
        List<ButtonMessage> allButtonMessages = new ArrayList<>();
        List<ButtonSuggestion> allButtonSuggestions = new ArrayList<>();
        final JsonParser jsonParser = new JsonParser();
        messagesHasButton.forEach(message -> {
            message = supportLogic.replaceMessageToButton(message);
            //System.out.println(message);
            JsonArray jsonArray = jsonParser.parse(message).getAsJsonArray();
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if(jsonObject.has("id")){
                    ButtonMessage buttonMessage = new Gson().fromJson(jsonObject, ButtonMessage.class);
                    allButtonMessages.add(buttonMessage);
                }else{
                    ButtonSuggestion buttonSuggestion = new Gson().fromJson(jsonObject, ButtonSuggestion.class);
                    allButtonSuggestions.add(buttonSuggestion);
                }
            });
        });

        //allButtonMessages.stream().forEach(p -> System.out.println(p.toString()));
        //System.out.println("=============================================");
        //allButtonSuggestions.stream().forEach(p -> System.out.println(p.toString()));

        Set<String> distinctButtonMessageId = allButtonMessages.stream()
                //.filter(p -> !(p.getPictureLink() instanceof String))
                .map(ButtonMessage::getId)
                .collect(Collectors.toSet());
        Set<String> disticntButtonSuggestionTitle = allButtonSuggestions.stream()
                .map(ButtonSuggestion::getTitle)
                .collect(Collectors.toSet());

        Map<ButtonMessage, Integer> buttonMessageShow = new HashMap<>();
        distinctButtonMessageId.stream().forEach(p -> {
            //System.out.println("Message: " + p);
            int counter = 0;
            ButtonMessage buttonMessage = null;
            for (ButtonMessage bm : allButtonMessages) {
                if(p.equalsIgnoreCase(bm.getId())){
                    //System.out.println("ID " + bm.getId());
                    buttonMessage = bm;
                    counter++;
                }
            }
            buttonMessageShow.put(buttonMessage, counter);
        });

        Map<ButtonSuggestion, Integer> buttonSuggestionShow = new HashMap<>();
        disticntButtonSuggestionTitle.stream().forEach(p -> {
            //System.out.println("Suggestion: " + p);
            int counter = 1;
            ButtonSuggestion buttonSuggestion = null;
            for (ButtonSuggestion bs : allButtonSuggestions) {
                if(p.equalsIgnoreCase(bs.getTitle())){
                    //System.out.println("Title " + bs.getTitle());
                    buttonSuggestion = bs;
                    counter++;
                }
            }
            buttonSuggestionShow.put(buttonSuggestion, counter);
        });

        // List<String> allMessageValues = listTicketDto.stream().map(p -> p.getMessage()).collect(Collectors.toList());


        Map<ButtonValues, Integer> buttonValuesMessage = new HashMap<>();
        Map<String, Integer> buttonHitByCustomer = new HashMap<>();
        for (ButtonMessage map : buttonMessageShow.keySet()) {
            //System.out.println("MESSAGE: " +  map + " VALUE: " + buttonMessageShow.get(map));
            List<ButtonValues> buttonValues = map.getButtonValues();
            buttonValues.stream().forEach(p -> {
                //System.out.println("Label: " + p.getName() + " Payload: " + p.getValue());
                int count = 0;
                ButtonValues bv = null;
                Set<String> accountIds = new HashSet<>();
                for (Ticket t : listTicketDto){
                    // System.out.println("String: " + s);
                    if(t.getMessage() != null && t.getMessage().equalsIgnoreCase(p.getValue())){
                        //System.out.println("Account Id: " + t.getAccount_id() + ", Match:" + p);
                        accountIds.add(t.getAccount_id());
                        bv = p;
                        count++;
                    }
                }
                if(count > 0){
                    buttonValuesMessage.put(bv, count);
                    String customerHit = map.getTitle() + ";" + bv.getName();
                    buttonHitByCustomer.put(customerHit, accountIds.size());
                }

            });
        }



        /*Map<ButtonValues, Integer> buttonValuesMessage = new HashMap<>();
        for (ButtonMessage map : buttonMessageShow.keySet()) {
            System.out.println("MESSAGE: " +  map + " VALUE: " + buttonMessageShow.get(map));
            List<ButtonValues> buttonValues = map.getButtonValues();
            buttonValues.stream().forEach(p -> {
                System.out.println("Label: " + p.getName() + " Payload: " + p.getValue());
                int count = 0;
                ButtonValues bv = null;
                for (String s : allMessageValues){
                    // System.out.println("String: " + s);
                    if(s.equalsIgnoreCase(p.getValue())){
                        System.out.println("Match: " + p);
                        bv = p;
                        count++;
                    }
                }
                if(count > 0){
                    buttonValuesMessage.put(bv, count);
                }

            });
        }*/

        // sorting button values
        Map<ButtonValues, Integer> treeMapBV = new TreeMap<ButtonValues, Integer>(
                (Comparator<? super ButtonValues>) (o1, o2) -> o2.getName().compareTo(o1.getName())).descendingMap();
        treeMapBV.putAll(buttonValuesMessage);
        for (ButtonValues map : treeMapBV.keySet()) {
            //System.out.println("Label: " +  map.getName() + " Payload: " + map.getValue() + " Total Hit: " + treeMapBV.get(map));
        }

        // sorting button per customer
        Map<String, Integer> treeMapCust = new TreeMap<String, Integer>(
                (Comparator<? super String>) (o1, o2) -> o2.compareTo(o1)).descendingMap();
        treeMapCust.putAll(buttonHitByCustomer);
        for (String s : treeMapCust.keySet()) {
            //System.out.println("Title: " + s + " Customer hit: " + treeMapCust.get(s));
        }

        // sorting button show up
        Map<ButtonMessage, Integer> treeMapBMShow = new TreeMap<ButtonMessage, Integer>(
                (Comparator<? super ButtonMessage>) (o1, o2) -> o2.getTitle().compareTo(o1.getTitle())).descendingMap();
        treeMapBMShow.putAll(buttonMessageShow);
        for (ButtonMessage bm : treeMapBMShow.keySet()) {
            //System.out.println("Button Title: " + bm.getTitle() + " count: " + treeMapBMShow.get(bm));
        }

        for (ButtonSuggestion map : buttonSuggestionShow.keySet()) {
            //System.out.println("SUGGESTION: " +  map + " VALUE: " + buttonSuggestionShow.get(map));
        }


        log.info("Total button out: " + allButtonMessages.size());
        log.info("Set button out: " + distinctButtonMessageId.size());

        //CREATE EXCEL------------------------------------------------------------------------
        Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);

        XSSFSheet sheet = workbook.createSheet("Ticket Summary");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));

        XSSFRow judul = sheet.createRow(0);
        judul.createCell(0).setCellValue(headerTitle);
        judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
        short x = 40 * 20;
        judul.setHeight(x);

        XSSFRow date = sheet.createRow(3);
        date.createCell(0).setCellValue(dateTitle);
        date.getCell(0).setCellStyle(mapStyle.get(BOLD_13));

        XSSFRow tableHeader = sheet.createRow(4);
        tableHeader.createCell(0).setCellValue("Channel Type");
        tableHeader.createCell(1).setCellValue("Ticket");
        tableHeader.createCell(2).setCellValue("Responded");
        tableHeader.createCell(3).setCellValue("Within SLA");
        tableHeader.createCell(4).setCellValue("Over SLA");
        tableHeader.createCell(5).setCellValue("Max Response Time");
        tableHeader.createCell(6).setCellValue("Min Response Time");
        tableHeader.createCell(7).setCellValue("Average Response Time");


        List<Integer> channels = new ArrayList<>();
        if(totalWebchatTicket.size() > 0) channels.add(1);
        if(totalLineTicket.size() > 0) channels.add(2);
        if(totalFacebookChat.size() > 0) channels.add(3);

        int dataRow = tableHeader.getRowNum() + 1;
        for (Integer order : channels) {
            if(order == 1){
                XSSFRow webchatRow = sheet.createRow(dataRow);
                webchatRow.createCell(0).setCellValue("Webchat");
                webchatRow.createCell(1).setCellValue(totalWebchatTicket.size());
                webchatRow.createCell(2).setCellValue(totalWebchatTicketRespondedByAgentParent.size());
                webchatRow.createCell(3).setCellValue(totalWebchatWithInSLA);
                webchatRow.createCell(4).setCellValue(totalWebchatNotWithInSLA);
                webchatRow.createCell(5).setCellValue(maxRespondWebchat);
                webchatRow.createCell(6).setCellValue(minRespondWebchat);
                webchatRow.createCell(7).setCellValue(averageRespondWebchat);
                dataRow++;
            }
            if(order == 2){
                XSSFRow lineRow = sheet.createRow(dataRow);
                lineRow.createCell(0).setCellValue("Linebot");
                lineRow.createCell(1).setCellValue(totalLineTicket.size());
                lineRow.createCell(2).setCellValue(totalLineTicketRespondedByAgentParent.size());
                lineRow.createCell(3).setCellValue(totalLineWithInSLA);
                lineRow.createCell(4).setCellValue(totalLineNotWithInSLA);
                lineRow.createCell(5).setCellValue(maxRespondLine);
                lineRow.createCell(6).setCellValue(minRespondLine);
                lineRow.createCell(7).setCellValue(averageRespondLine);
                dataRow++;
            }
            if(order == 3){
                XSSFRow facebookRow = sheet.createRow(dataRow);
                facebookRow.createCell(0).setCellValue("Facebook");
                facebookRow.createCell(1).setCellValue(totalFacebookChat.size());
                facebookRow.createCell(2).setCellValue(totalFacebookChatRespondedByAgent.size());
                facebookRow.createCell(3).setCellValue(totalFacebookWithInSLA);
                facebookRow.createCell(4).setCellValue(totalFacebookNotWithInSLA);
                facebookRow.createCell(5).setCellValue(maxRespondFacebook);
                facebookRow.createCell(6).setCellValue(minRespondFacebook);
                facebookRow.createCell(7).setCellValue(averageRespondFacebook);
                dataRow++;
            }
        }
        XSSFRow topBorderRow = sheet.createRow(dataRow);
        topBorderRow.createCell(0);
        topBorderRow.createCell(1);
        topBorderRow.createCell(2);
        topBorderRow.createCell(3);
        topBorderRow.createCell(4);
        topBorderRow.createCell(5);
        topBorderRow.createCell(6);
        topBorderRow.createCell(7);

        XSSFRow totalRow = sheet.createRow(topBorderRow.getRowNum() + 1);
        totalRow.createCell(0).setCellValue("Total");
        totalRow.createCell(1).setCellValue(totalTicket.size());
        totalRow.createCell(2).setCellValue(totalTicketRespondByAgent.size());
        totalRow.createCell(3).setCellValue(ticketSLA);
        totalRow.createCell(4).setCellValue(ticketNotSLA);
        totalRow.createCell(5).setCellValue(maxRespondAgent);
        totalRow.createCell(6).setCellValue(minRespondAgent);
        totalRow.createCell(7).setCellValue(averageRespondAgent);

        // button show
        XSSFRow btnShowHeader = sheet.createRow(totalRow.getRowNum() + 2);
        btnShowHeader.createCell(0).setCellValue("Button Show Up");
        btnShowHeader.createCell(1).setCellValue("Count");
        btnShowHeader.getCell(0).setCellStyle(mapStyle.get(BOLD_12));
        btnShowHeader.getCell(1).setCellStyle(mapStyle.get(BOLD_12));

        int btnCounter = btnShowHeader.getRowNum() + 1;
        for (ButtonMessage map : treeMapBMShow.keySet()) {
            XSSFRow btnValue = sheet.createRow(btnCounter);
            btnValue.createCell(0).setCellValue(map.getTitle());
            btnValue.createCell(1).setCellValue(treeMapBMShow.get(map));
            btnCounter++;
        }

        // button hit
        btnShowHeader = sheet.createRow(btnCounter + 1);
        btnShowHeader.createCell(0).setCellValue("Button");
        btnShowHeader.createCell(1).setCellValue("Label");
        btnShowHeader.createCell(2).setCellValue("Total Hit");
        btnShowHeader.createCell(3).setCellValue("Total Hit by Customer");
        btnShowHeader.getCell(0).setCellStyle(mapStyle.get(BOLD_12));
        btnShowHeader.getCell(1).setCellStyle(mapStyle.get(BOLD_12));
        btnShowHeader.getCell(2).setCellStyle(mapStyle.get(BOLD_12));
        btnShowHeader.getCell(3).setCellStyle(mapStyle.get(BOLD_12));

        btnCounter = btnShowHeader.getRowNum() + 1;
        for (String s : treeMapCust.keySet()) {
            // CARI CABANG;Bantuan Live Agent
            String[] splitTitleLabel = s.split(";");
            String title = splitTitleLabel[0];
            String label = splitTitleLabel[1];
            XSSFRow btnTitle = sheet.createRow(btnCounter);
            btnTitle.createCell(0).setCellValue(title);
            btnTitle.createCell(1).setCellValue(label);
            for (ButtonValues bv : treeMapBV.keySet()) {
                if(label.equalsIgnoreCase(bv.getName())){
                    String totalHit = String.valueOf(treeMapBV.get(bv));
                    String totalHitPerCust = String.valueOf(treeMapCust.get(s));
                    btnTitle.createCell(2).setCellValue(totalHit);
                    btnTitle.createCell(3).setCellValue(totalHitPerCust);
                }
            }
            btnCounter++;
        }

        // set cell style
        for (int i = 0; i < tableHeader.getPhysicalNumberOfCells(); i++){
            tableHeader.getCell(i).setCellStyle(mapStyle.get(BOLD_12));
            topBorderRow.getCell(i).setCellStyle(mapStyle.get(BOLD_11_TOP_BORDER));
            totalRow.getCell(i).setCellStyle(mapStyle.get(BOLD_11));
        }

        // set auto column
        int a = tableHeader.getPhysicalNumberOfCells();
        for (int i = 0; i < a; i++) {
            sheet.autoSizeColumn(i);
        }
        log.info("END PRINT SUMMARY");
    }

    @Override
    public void geTicketXSSFRespondAgentTicketParentOnly(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, String sheetName, String mapId) {
        try {
            List<Ticket> list = service.getTicketListFromJsonFile(fileInput);
            // get ticket reply_agent != bot
            List<Ticket> listTicket = getParentTicketRespondByAgent(list);
            List<Ticket> listSortedByDate = fixedValueAttribute(listTicket, mapId).stream()
                    .sorted(Comparator.comparing(Ticket::getCreated_date))
                    .collect(Collectors.toList());
            //List<Ticket> sortedByDateList = listFixTicket.stream().sorted(Comparator.comparing(Ticket::getCreated_date)).collect(Collectors.toList());

            Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);

            XSSFSheet sheet = workbook.createSheet("Ticket Responded");
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));
            XSSFRow judul = sheet.createRow(0);
            judul.createCell(0).setCellValue("Ticket Respond By Agent");
            judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
            short x = 40 * 20;
            judul.setHeight(x);

            // disini
            createTicketRowColumnXSSF(workbook, sheet, listSortedByDate, judul.getRowNum() + 3, startRow);
            log.info("FINISH CREATE PARENT TICKET");
        } catch (Exception e) {
            log.error("Error: " + e);
        }
    }

    @Override
    public void geTicketXSSFRespondAgentOnlyNewPivot(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, String sheetName, String mapId) {
        try {
            List<Ticket> list = service.getTicketListFromJsonFile(fileInput);

            // get list ticket number handled by agent in string
            Set<String> listTicketNumberHandleByAgent = list.stream()
                    .filter(x -> x.getReply_agent() != null && !x.getReply_agent().equalsIgnoreCase("bot"))
                    .map(Ticket::getTicket_number).collect(Collectors.toSet());

            // get list ticket parent which handled by agent
            List<Ticket> listTicketHandleByAgent = list.stream()
                    .filter(x -> listTicketNumberHandleByAgent.contains(x.getTicket_number())
                            && x.getRedistribute() != null && x.getRedistribute().equalsIgnoreCase("false"))
                    //.sorted(Comparator.comparing(Ticket::getCreated_date))
                    .collect(Collectors.toList());

            // breakdown message handled by agent
            List<Ticket> listTicketDto1 = list.stream()
                    .filter(x -> listTicketNumberHandleByAgent.contains(x.getTicket_number()))
                    .sorted(Comparator.comparing(Ticket::getTicket_number).thenComparing(Ticket::getCreated_date))
                    .collect(Collectors.toList());
            List<Ticket> listTicketDto2 = fixedValueAttribute(listTicketDto1, mapId);

            Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);

            XSSFSheet sheet = workbook.createSheet("Detail Responded Message");
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));
            XSSFRow judul = sheet.createRow(0);
            judul.createCell(0).setCellValue("Detail Messages");
            judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
            short x = 40 * 20;
            judul.setHeight(x);

            // create row
            createTicketRowColumnXSSFNewPivot(sheet, listTicketHandleByAgent, listTicketDto2, judul.getRowNum() + 3, startRow);

            log.info("FINISH DETAIL RESPOND MESSAGE");
        } catch (Exception e) {
            log.info("Error: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void getTicketXSSFAllCategory(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, String sheetName, String mapId) {
        try {
            List<Ticket> list = service.getTicketListFromJsonFile(fileInput);
            List<Ticket> listTicketDto0 = list.stream()
                    .sorted(Comparator.comparing(Ticket::getTicket_number).thenComparing(Ticket::getCreated_date))
                    .collect(Collectors.toList());
            List<Ticket> listTicketDto = fixedValueAttribute(listTicketDto0, mapId);

            Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);

            XSSFSheet sheet = workbook.createSheet("All ticket");
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 10));
            XSSFRow judul = sheet.createRow(0);
            judul.createCell(0).setCellValue("All Tickets");
            judul.getCell(0).setCellStyle(mapStyle.get(SupportLogic.HEADER_STYLE));
            short x = 40 * 20;
            judul.setHeight(x);

            // disini
            createTicketRowColumnXSSF(workbook, sheet, listTicketDto, judul.getRowNum() + 3, startRow);

            log.info("FINISH CREATE ALL CATEGORY ROW");
        } catch (Exception e) {
            log.error("Error: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void getTicketVersionXSSF(String fileInput, String fileOutput, int startRow, int endRow, String sheetName) {

    }

    @Override
    public void geTicketXSSFRespondAgentOnly(XSSFWorkbook workbook, String fileInput, String fileOutput, int startRow, int endRow, String sheetName) {

    }

    /**
     * add data to row excel agent pivot style
     *
     * @param sheet
     * @param listTicketHandleByAgent
     * @param listTicketDto
     * @param startRowXSSF
     * @param startRow
     */
    private void createTicketRowColumnXSSFNewPivot(XSSFSheet sheet, List<Ticket> listTicketHandleByAgent, List<Ticket> listTicketDto, int startRowXSSF, int startRow) {
        List<Ticket> listUniqueTicket = new ArrayList<>();
        listUniqueTicket.addAll(listTicketHandleByAgent);

        int startOriginalTicket = startRowXSSF;
        for(Ticket tiketDto : listUniqueTicket) {
            String ticketNumber = tiketDto.getTicket_number();
            XSSFRow row1 = sheet.createRow((short) startOriginalTicket);
            row1.createCell(0).setCellValue("Created Date:");
            row1.createCell(1).setCellValue(tiketDto.getCreated_date());
            row1.createCell(2).setCellValue("Unassigned Date:");
            row1.createCell(3).setCellValue(tiketDto.getUnassigned_date_str());

            XSSFRow row2 = sheet.createRow((short) startOriginalTicket + 1);
            row2.createCell(0).setCellValue("Created Date Str:");
            row2.createCell(1).setCellValue(tiketDto.getCreated_date_str());
            row2.createCell(2).setCellValue("Unassigned Duration:");
            row2.createCell(3).setCellValue(tiketDto.getUnassign_duration());

            XSSFRow row3 = sheet.createRow((short) startOriginalTicket + 2);
            row3.createCell(0).setCellValue("Ticket Number:");
            row3.createCell(1).setCellValue(tiketDto.getTicket_number());
            row3.createCell(2).setCellValue("Assigned Date:");
            row3.createCell(3).setCellValue(tiketDto.getAssigned_date_str());

            XSSFRow row4 = sheet.createRow((short) startOriginalTicket + 3);
            row4.createCell(0).setCellValue("Status:");
            row4.createCell(1).setCellValue(tiketDto.getStatus());
            row4.createCell(2).setCellValue("Assigned Duration:");
            row4.createCell(3).setCellValue(tiketDto.getAssign_duration());

            XSSFRow row5 = sheet.createRow((short) startOriginalTicket + 4);
            row5.createCell(0).setCellValue("Account Name:");
            row5.createCell(1).setCellValue(tiketDto.getAccount_name());
            row5.createCell(2).setCellValue("Open Date:");
            row5.createCell(3).setCellValue(tiketDto.getOpen_date_str());

            XSSFRow row6 = sheet.createRow((short) startOriginalTicket + 5);
            row6.createCell(0).setCellValue("Account Id:");
            row6.createCell(1).setCellValue(tiketDto.getAccount_id());
            row6.createCell(2).setCellValue("Open Duration:");
            row6.createCell(3).setCellValue(tiketDto.getOpen_duration());

            XSSFRow row7 = sheet.createRow((short) startOriginalTicket + 6);
            row7.createCell(0).setCellValue("Account Screen:");
            row7.createCell(1).setCellValue(tiketDto.getAccount_screen());
            row7.createCell(2).setCellValue("Pending Date:");
            row7.createCell(3).setCellValue(tiketDto.getPending_date_str());

            XSSFRow row8 = sheet.createRow((short) startOriginalTicket + 7);
            row8.createCell(0).setCellValue("Response Time:");
            row8.createCell(1).setCellValue(tiketDto.getResponse_time());
            row8.createCell(2).setCellValue("Pending Duration:");
            row8.createCell(3).setCellValue(tiketDto.getPending_duration());

            XSSFRow row9 = sheet.createRow((short) startOriginalTicket + 8);
            row9.createCell(0).setCellValue("Closure Type:");
            row9.createCell(1).setCellValue(tiketDto.getClosure_type());
            row9.createCell(2).setCellValue("Closed Date:");
            row9.createCell(3).setCellValue(tiketDto.getClosed_date_str());

            XSSFRow row10 = sheet.createRow((short) startOriginalTicket + 9);
            row10.createCell(0).setCellValue("Remark:");
            row10.createCell(1).setCellValue(tiketDto.getRemark());
            row10.createCell(2).setCellValue("Closed Interval:");
            row10.createCell(3).setCellValue(tiketDto.getClose_interval());

            XSSFRow row11 = sheet.createRow((short) startOriginalTicket + 10);
            row11.createCell(0).setCellValue("Closed By:");
            row11.createCell(1).setCellValue(tiketDto.getClosed_by());
            //row11.createCell(2).setCellValue("Created Date Str:");
            //row11.createCell(3).setCellValue(tiketDto.getCreated_date_str());
            row11.createCell(2).setCellValue("Transfered:");
            row11.createCell(3).setCellValue(tiketDto.getTransfered());

            XSSFRow row12 = sheet.createRow((short) startOriginalTicket + 11);
            row12.createCell(0).setCellValue("With in SLA:");
            row12.createCell(1).setCellValue(tiketDto.getWithin_sla());
            row12.createCell(2).setCellValue("Escalated:");
            row12.createCell(3).setCellValue(tiketDto.getEscalated());

            XSSFRow row13 = sheet.createRow((short) startOriginalTicket + 12);
            row13.createCell(0).setCellValue("Channel Type:");
            row13.createCell(1).setCellValue(tiketDto.getChannel_type());
            row13.createCell(2).setCellValue("Supervisor:");
            row13.createCell(3).setCellValue(tiketDto.getSupervisor());

            XSSFRow row14 = sheet.createRow((short) startOriginalTicket + 13);
            row14.createCell(0).setCellValue("Subject:");
            row14.createCell(1).setCellValue(tiketDto.getSubject());


            List<Ticket> ticketNumbers = listTicketDto.stream()
                    .filter(p -> p.getTicket_number().equalsIgnoreCase(ticketNumber))
                    .collect(Collectors.toList());

            int startMessage = row14.getRowNum() + 2;
            int nextRow = 0;
            for (Ticket tiket : ticketNumbers) {
                int firstRow = startMessage + nextRow;
                int lastRow = firstRow;
                int firstCol = 0;
                int lastCol = 9;
                //System.out.println("FirstRow:" + firstRow + " lastRow:" + lastRow + " firstCol:" + firstCol + " lastCol:" + lastCol);
                sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
                XSSFRow msgRow = sheet.createRow((short) startMessage + nextRow);
                msgRow.createCell(0).setCellValue(getMessageRow(tiket.getCreated_date(), tiket.getCreated_date_str(), tiket.getAnswer(), tiket.getAccount_name(), tiket.getReply_agent(), tiket.getMessage()));
                nextRow++;
            }
            int endMessage = startMessage + nextRow;
            //System.out.println("startMessage:" + startMessage + " endMessage:" + endMessage + " nextRow:" + nextRow);
            sheet.addMergedRegion(new CellRangeAddress(endMessage, endMessage, 0, 9));
            XSSFRow row15 = sheet.createRow((short) endMessage);
            row15.createCell(0).setCellValue("===================================================================");
            //System.out.println("END ROW: " + endMessage);
            startOriginalTicket = endMessage + 2;
        }

        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(1, 40*256);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
    }

    /**
     * add data to row excel
     *
     * @param workbook
     * @param sheet
     * @param listTicketDto
     * @param startRowXSSF
     * @param startRow
     */
    private void createTicketRowColumnXSSF(XSSFWorkbook workbook, XSSFSheet sheet, List<Ticket> listTicketDto, int startRowXSSF, int startRow) {
        XSSFRow rowdata = sheet.createRow((short) startRowXSSF);

        rowdata.createCell(0).setCellValue("created_date");
        rowdata.createCell(1).setCellValue("created_date_str");
        rowdata.createCell(2).setCellValue("ticket_number");
        rowdata.createCell(3).setCellValue("status");
        rowdata.createCell(4).setCellValue("account_name");
        rowdata.createCell(5).setCellValue("message");
        rowdata.createCell(6).setCellValue("answer");
        rowdata.createCell(7).setCellValue("reply_agent");
        rowdata.createCell(8).setCellValue("assigned_agent");
        rowdata.createCell(9).setCellValue("response_time");
        rowdata.createCell(10).setCellValue("response_time_agent");
        rowdata.createCell(11).setCellValue("account_id");
        rowdata.createCell(12).setCellValue("account_screen");
        rowdata.createCell(13).setCellValue("closure_type");
        rowdata.createCell(14).setCellValue("remark");
        rowdata.createCell(15).setCellValue("closed_by");
        rowdata.createCell(16).setCellValue("within_sla");
        rowdata.createCell(17).setCellValue("unassigned_date_str");
        rowdata.createCell(18).setCellValue("unassign_duration");
        rowdata.createCell(19).setCellValue("assigned_date_str");
        rowdata.createCell(20).setCellValue("assign_duration");
        rowdata.createCell(21).setCellValue("open_date_str");
        rowdata.createCell(22).setCellValue("open_duration");
        rowdata.createCell(23).setCellValue("pending_date_str");
        rowdata.createCell(24).setCellValue("pending_duration");
        rowdata.createCell(25).setCellValue("closed_date_str");
        rowdata.createCell(26).setCellValue("close_interval");
        rowdata.createCell(27).setCellValue("transfered");
        rowdata.createCell(28).setCellValue("escalated");
        rowdata.createCell(29).setCellValue("transfered_date_str");
        rowdata.createCell(30).setCellValue("transfered_from");
        rowdata.createCell(31).setCellValue("transfered_to");
        rowdata.createCell(32).setCellValue("supervisor");
        rowdata.createCell(33).setCellValue("subject");
        rowdata.createCell(34).setCellValue("channel_type");
        rowdata.createCell(35).setCellValue("Parent");
        rowdata.createCell(36).setCellValue("Redistribute");
        rowdata.createCell(37).setCellValue("Redistribute_agent");

        int startData = startRowXSSF + 1;
        XSSFRow rowisi = null;
        for (int i = startRow; i < listTicketDto.size(); i++) {
            rowisi = sheet.createRow((short)startData+i);

            rowisi.createCell(0).setCellValue(listTicketDto.get(i).getCreated_date());
            rowisi.createCell(1).setCellValue(listTicketDto.get(i).getCreated_date_str());
            rowisi.createCell(2).setCellValue(listTicketDto.get(i).getTicket_number());
            rowisi.createCell(3).setCellValue(listTicketDto.get(i).getStatus());
            rowisi.createCell(4).setCellValue(listTicketDto.get(i).getAccount_name());
            if (listTicketDto.get(i).getMessage() == null) {
                rowisi.createCell(5).setCellValue("NULL");
            } else {
                rowisi.createCell(5).setCellValue(avoidMessageMaximumLength(listTicketDto.get(i).getMessage()));
            }

            rowisi.createCell(6).setCellValue(listTicketDto.get(i).getAnswer());
            rowisi.createCell(7).setCellValue((listTicketDto.get(i).getReply_agent()));
            rowisi.createCell(8).setCellValue((listTicketDto.get(i).getAssigned_agent()));
            rowisi.createCell(9).setCellValue(listTicketDto.get(i).getResponse_time());
            rowisi.createCell(10).setCellValue(listTicketDto.get(i).getResponse_time_agent());
            rowisi.createCell(11).setCellValue((listTicketDto.get(i).getAccount_id()));
            rowisi.createCell(12).setCellValue(listTicketDto.get(i).getAccount_screen());
            rowisi.createCell(13).setCellValue(listTicketDto.get(i).getClosure_type());
            rowisi.createCell(14).setCellValue(listTicketDto.get(i).getRemark());
            rowisi.createCell(15).setCellValue((listTicketDto.get(i).getClosed_by()));
            rowisi.createCell(16).setCellValue(listTicketDto.get(i).getWithin_sla());
            rowisi.createCell(17).setCellValue(listTicketDto.get(i).getUnassigned_date_str());
            rowisi.createCell(18).setCellValue(listTicketDto.get(i).getUnassign_duration());
            rowisi.createCell(19).setCellValue(listTicketDto.get(i).getAssigned_date_str());
            rowisi.createCell(20).setCellValue(listTicketDto.get(i).getAssign_duration());
            rowisi.createCell(21).setCellValue(listTicketDto.get(i).getOpen_date_str());
            rowisi.createCell(22).setCellValue(listTicketDto.get(i).getOpen_duration());
            rowisi.createCell(23).setCellValue(listTicketDto.get(i).getPending_date_str());
            rowisi.createCell(24).setCellValue(listTicketDto.get(i).getPending_duration());
            rowisi.createCell(25).setCellValue(listTicketDto.get(i).getClosed_date_str());
            rowisi.createCell(26).setCellValue(listTicketDto.get(i).getClose_interval());
            rowisi.createCell(27).setCellValue(listTicketDto.get(i).getTransfered());
            rowisi.createCell(28).setCellValue(listTicketDto.get(i).getEscalated());
            rowisi.createCell(29).setCellValue(listTicketDto.get(i).getTransfered_date_str());
            rowisi.createCell(30).setCellValue((listTicketDto.get(i).getTransfered_from()));
            rowisi.createCell(31).setCellValue((listTicketDto.get(i).getTransfered_to()));
            rowisi.createCell(32).setCellValue((listTicketDto.get(i).getSupervisor()));
            rowisi.createCell(33).setCellValue(listTicketDto.get(i).getSubject());
            rowisi.createCell(34).setCellValue(listTicketDto.get(i).getChannel_type());
            rowisi.createCell(35).setCellValue(listTicketDto.get(i).getParent());
            rowisi.createCell(36).setCellValue(listTicketDto.get(i).getRedistribute());
            rowisi.createCell(37).setCellValue(listTicketDto.get(i).getRedistribute_agent());
        }

        Map<String, XSSFCellStyle> mapStyle = supportLogic.getMapStyle(workbook);

        // set cell style
        for (int i = 0; i < rowdata.getPhysicalNumberOfCells(); i++){
            rowdata.getCell(i).setCellStyle(mapStyle.get(SupportLogic.BOLD_12));
        }

        // set auto column
        int a = rowdata.getPhysicalNumberOfCells();
        for (int i = 0; i < a; i++) {
            if(i == 5 || i == 14 || i == 33) {
                continue;
            }
            sheet.autoSizeColumn(i);
        }
    }


    /**
     * filter list to specific channel
     *
     * @param parentTickets must contains list ticket filter by parent == 1 and redistribute == false (get parent ticket)
     * @param channelType
     * @return
     */
    private List<Ticket> getListPerchannelType(List<Ticket> parentTickets, String channelType) {
        if (channelType == "facebook" || channelType == "facebook;chat") {
            List<Ticket> listPerchannel = parentTickets.stream()
                    .filter(p -> p.getChannel_type().equalsIgnoreCase("facebook;chat") || p.getChannel_type().equalsIgnoreCase("facebook"))
                    .collect(Collectors.toList());
            return listPerchannel;
        }

        List<Ticket> listPerchannel = parentTickets.stream()
                .filter(p -> p.getChannel_type().equalsIgnoreCase(channelType))
                .collect(Collectors.toList());
        return listPerchannel;
    }

    /**
     * filter by all tickets to agent
     *
     * @param parentTickets must contains list ticket filter by parent == 1 and redistribute == false (get parent ticket)
     * @return
     */
    private Set<String> getTicketToAgent(List<Ticket> parentTickets){
        Set<String> ticketsToAgent = parentTickets.stream()
                .filter(p -> p.getStatus() != null && p.getStatus().equalsIgnoreCase("closed") &&
                        p.getAssigned_agent() != null && !p.getAssigned_agent().equalsIgnoreCase("bot"))
                .map(Ticket::getTicket_number)
                .collect(Collectors.toSet());
        return ticketsToAgent;
    }

    /**
     * filter list by reply_agent != "Bot" to get ticket where agent has reply
     *
     * @param allTickets this is list must contains all ticket rows from json file
     * @return
     */
    private List<Ticket> getParentTicketRespondByAgent(List<Ticket> allTickets) {
        // distinct ticket where agent has reply
        Set<String> totalTicketRespondedByAgent = allTickets.stream()
                .filter(x -> x.getReply_agent() != null && !x.getReply_agent().equalsIgnoreCase("bot"))
                .map(Ticket::getTicket_number).collect(Collectors.toSet());
        // get ticket parent from distinct list
        List<Ticket> totalParentTicketRespondedByAgent = allTickets.stream()
                .filter(x -> totalTicketRespondedByAgent.contains(x.getTicket_number())
                        && x.getRedistribute() != null && x.getRedistribute().equalsIgnoreCase("false"))
                .sorted(Comparator.comparing(Ticket::getTicket_number).thenComparing(Ticket::getCreated_date))
                .collect(Collectors.toList());
        return totalParentTicketRespondedByAgent;
    }

    /**
     * filter ticket number from ticketsToAgent != ticketsRespondByAgent
     *
     * @param ticktesToAgent
     * @param totalTicketRespondByAgent
     * @return
     */
    private Set<String> getTicketClosedUnrespondByAgent(Set<String> ticktesToAgent, List<Ticket> totalTicketRespondByAgent){
        // get only ticket number
        Set<String> ticketNumber = totalTicketRespondByAgent.stream()
                .map(Ticket::getTicket_number)
                .collect(Collectors.toSet());
        // get ticket number to agent where not equal to respond by agent ticket
        Set<String> tickets = ticktesToAgent.stream()
                .filter(p -> !ticketNumber.contains(p))
                .collect(Collectors.toSet());
        return tickets;
    }

    /**
     * filter respond tickets by perchannel type
     *
     * @param respondTickets must contains all list has filter by reply_agent != Bot (Respond Tickets by Agent)
     * @return
     */
    private List<Ticket> getTicketRespondPerchannel(List<Ticket> respondTickets, String channelType) {
        if (channelType == "facebook" || channelType == "facebook;chat") {
            List<Ticket> respondTicketsPerchannel = respondTickets.stream()
                    .filter(p -> p.getChannel_type().equalsIgnoreCase("facebook;chat") || p.getChannel_type().equalsIgnoreCase("facebook"))
                    .collect(Collectors.toList());
            return respondTicketsPerchannel;
        }
        List<Ticket> respondTicketsPerchannel = respondTickets.stream()
                .filter(p -> p.getChannel_type().equalsIgnoreCase(channelType))
                .collect(Collectors.toList());
        return respondTicketsPerchannel;
    }

    /**
     * count ticket where withInSLA true
     *
     * @param parentTickets must contains ticket parent filter by respond agent
     * @return
     */
    private long countTicketsRespondWithInSLA(List<Ticket> parentTickets) {
        long countTickets = parentTickets.stream()
                .filter(p -> p.getWithin_sla() != null && p.getWithin_sla().equalsIgnoreCase("true"))
                .count();
        return countTickets;
    }

    /**
     * count ticket where withInSLA not true
     *
     * @param parentTickets must contains ticket parent filter by respond agent
     * @return
     */
    private long countNotTicketsRespondWithInSLA(List<Ticket> parentTickets) {
        long countTickets = parentTickets.stream()
                .filter(p -> p.getWithin_sla() == null || !p.getWithin_sla().equalsIgnoreCase("true"))
                .count();
        return countTickets;
    }

    /**
     * Get maximum respond time
     *
     * @param ticketsParent ticket respond by agent
     * @return
     */
    private String getMaxRespondAgent(List<Ticket> ticketsParent){
        Long maxRespondSecond = ticketsParent.stream().mapToLong(p -> Long.parseLong(p.getResponse_time())).max().orElse(0);
        return supportLogic.getHourMinuteSecond(maxRespondSecond);
    }

    /**
     * Get minimum respond time
     *
     * @param ticketsParent ticket respond by agent
     * @return
     */
    private String getMinRespondAgent(List<Ticket> ticketsParent){
        Long minRespondSecond = ticketsParent.stream().mapToLong(p -> Long.parseLong(p.getResponse_time())).min().orElse(0);
        return supportLogic.getHourMinuteSecond(minRespondSecond);
    }

    /**
     * Get average respond time
     *
     * @param ticketsParent ticket respond by agent
     * @return
     */
    private String getAverageRespondAgent(List<Ticket> ticketsParent){
        Long averageRespondAgent = (long) ticketsParent.stream().mapToLong(p -> Long.parseLong(p.getResponse_time())).average().orElse(0);
        return  supportLogic.getHourMinuteSecond(averageRespondAgent);
    }

    /**
     * Replace id agent & supervisor to readable name
     *
     * @param tickets
     * @param mapId
     * @return
     */
    private List<Ticket> fixedValueAttribute(List<Ticket> tickets, String mapId){
        tickets.stream().forEach(x -> {
            x.setAssigned_agent(replaceIdToName(x.getAssigned_agent(), mapId));
            x.setReply_agent(replaceIdToName(x.getReply_agent(), mapId));
            x.setAccount_id(replaceIdToName(x.getAccount_id(), mapId));
            x.setClosed_by(replaceIdToName(x.getClosed_by(), mapId));
            x.setAssigned_agent(replaceIdToName(x.getAssigned_agent(), mapId));
            x.setSupervisor(replaceIdToName(x.getSupervisor(), mapId));
            x.setTransfered_from(replaceIdToName(x.getTransfered_from(), mapId));
            x.setTransfered_to(replaceIdToName(x.getTransfered_to(), mapId));
            x.setBot_id(replaceIdToName(x.getBot_id(), mapId));
            x.setRemark(supportLogic.removeHtmlTag(x.getRemark()));
            x.setMessage(replaceMessageWithPictureLink(x.getMessage(), x.getPicture_link()));
        });
        return tickets;
    }

    /**
     * Replace id agent/supervisor to real name
     *
     * @param id
     * @param mapId "acc" or "fif"
     * @return
     */
    private String replaceIdToName(String id, String mapId) {
        if (id == null) {
            return "";
        }

        Map<String, String> map = new HashMap<>();
        if(mapId.equalsIgnoreCase("acc")) map = mapAcc;
        else if(mapId.equalsIgnoreCase("fif")) map = mapFIF;

        String[] x = id.split(",");
        for (int i = 0; i < x.length; i++) {
            for (String string : map.keySet()) {
                id = id.replace(string, map.get(string));
            }
        }
        return id;
    }

    /**
     * bundle description in a message
     *
     * @param createDate
     * @param createDateStr
     * @param answer
     * @param accountName
     * @param replyAgent
     * @param message
     * @return
     */
    private String getMessageRow(String createDate, String createDateStr, String answer, String accountName, String replyAgent, String message) {
        StringBuilder builder = new StringBuilder()
                .append("(" + createDate + ")")
                .append("(" + createDateStr + ")")
                .append(" ");

        switch (answer) {
            case "false":
                builder.append(accountName + ":");
                break;
            case "true":
                builder.append(replyAgent + ":");
                break;
        }
        builder.append(" " + avoidMessageMaximumLength(message));
        return builder.toString();
    }

    /**
     * if message is null then possibility it output a picture
     *
     * @param message
     * @param pictureLink
     * @return
     */
    private String replaceMessageWithPictureLink(String message, String pictureLink) {
        return (message == null || message.isEmpty()) ? pictureLink : message;
    }

    /**
     * The maximum length of cell contents (text) is 32,767 characters
     *
     * @param message
     * @return
     */
    private String avoidMessageMaximumLength(String message){
        if(message == null) return message;
        int length = message.length();
        //System.out.println("## " + length + " ## " + message);
        if(length > maximumLength) message = message.substring(0, (maximumLength));
        return message;
    }

    /**
     * Create style to excel cell
     *
     * @param workbook
     * @return
     */
    /*private Map<String, XSSFCellStyle> getMapStyle(XSSFWorkbook workbook) {
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

        return cellStyles;
    }*/
}
