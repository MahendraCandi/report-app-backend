package com.mahendracandi.chatbotgeneratereportapp.common;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mahendracandi.chatbotgeneratereportapp.model.Button;

public class ButtonMessageDeserializer implements JsonDeserializer<Button> {
    @Override
    public Button deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        /*JsonObject jsonObject = json.getAsJsonObject();
        ButtonMessage bm = new ButtonMessage();
        ButtonSuggestion bs = new ButtonSuggestion();
        Gson gson = new Gson();
        if(jsonObject.equals(bm)){
            bm = gson.fromJson(json, ButtonMessage.class);
            return bm;
        }else if(jsonObject.equals(bs)){
            bs = gson.fromJson(json, ButtonSuggestion.class);
            return bs;
        }*/

        return null;
    }
}
