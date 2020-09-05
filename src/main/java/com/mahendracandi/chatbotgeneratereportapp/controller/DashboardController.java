package com.mahendracandi.chatbotgeneratereportapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class DashboardController {
	
	@RequestMapping("/")
	public String getIndexHtml() {
		return "index.html";
	}
}
