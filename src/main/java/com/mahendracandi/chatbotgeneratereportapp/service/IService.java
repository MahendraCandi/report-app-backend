package com.mahendracandi.chatbotgeneratereportapp.service;

import java.util.List;

import com.mahendracandi.chatbotgeneratereportapp.model.FallbackActivity;
import com.mahendracandi.chatbotgeneratereportapp.model.Knowledge;
import com.mahendracandi.chatbotgeneratereportapp.model.Ticket;

public interface IService {
    List<Ticket> getTicketListFromJsonFile(String fileName);

    List<FallbackActivity> getFallbackActivityFromJsonFile(String fileName);

    List<Knowledge> getListFromJsonFile(String fileName);

    Ticket[] getArrayFromJsonFile(String fileName);
}
