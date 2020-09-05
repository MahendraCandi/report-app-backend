package com.mahendracandi.chatbotgeneratereportapp.service.serviceImpl;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mahendracandi.chatbotgeneratereportapp.model.FallbackActivity;
import com.mahendracandi.chatbotgeneratereportapp.model.Knowledge;
import com.mahendracandi.chatbotgeneratereportapp.model.Ticket;
import com.mahendracandi.chatbotgeneratereportapp.service.IService;

@Service(value = "iService")
public class ServiceImpl implements IService {
	private static final Logger log = LogManager.getLogger(ServiceImpl.class);

    @Override
    public List<Ticket> getTicketListFromJsonFile(String fileName) {
        try {
            File file = new File(fileName);
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Type type = new TypeToken<List<Ticket>>(){}.getType();
            List<Ticket> list = new Gson().fromJson(jsonReader, type);
            return list;
        }catch (Exception e){
            log.error("Exception error: " + e);
        }
        return null;
    }

    @Override
    public List<FallbackActivity> getFallbackActivityFromJsonFile(String fileName) {
    	List<FallbackActivity> list = null;
    	String listData = null;
        try {
            File file = new File(fileName);
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            
            JsonElement jElement = JsonParser.parseReader(jsonReader);
            
            if (jElement.isJsonArray()) {
            	listData = jElement.getAsJsonArray().toString();
            } else if (jElement.isJsonObject()) {
            	listData = jElement.getAsJsonObject().get("response").getAsJsonObject().get("docs").toString();
            }
            
            Type type = new TypeToken<List<FallbackActivity>>(){}.getType();
            list = new Gson().fromJson(listData, type);
            return list;
        }catch (Exception e){
            log.error("Exection error: " + e);
        }
        return null;
    }

    @Override
    public List<Knowledge> getListFromJsonFile(String fileName) {
        try {
            File file = new File(fileName);
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Type type = new TypeToken<List<Knowledge>>(){}.getType();
            List<Knowledge> list = new Gson().fromJson(jsonReader, type);
            return list;
        }catch (Exception e){
            log.error("Exception error: " + e);
        }
        return null;
    }

    public Ticket[] getArrayFromJsonFile(String fileName){
        try {
            File file = new File(fileName);
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Type type = new TypeToken<List<Ticket>>(){}.getType();
            Ticket[] list = new Gson().fromJson(jsonReader, type);
            return list;
        }catch (Exception e){
            log.error("Exception error: " + e);
        }
        return null;
    }
}
