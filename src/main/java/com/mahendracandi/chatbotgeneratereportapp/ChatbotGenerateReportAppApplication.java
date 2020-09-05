package com.mahendracandi.chatbotgeneratereportapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.mahendracandi.chatbotgeneratereportapp.console.TextAreaOutputStreamTest;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mahendracandi.chatbotgeneratereportapp"})
public class ChatbotGenerateReportAppApplication {

	public static void main(String[] args) {
//		if (args.length == 0) {
//			// show console
//	        TextAreaOutputStreamTest.mainRunner(args);
//	    }
		SpringApplication.run(ChatbotGenerateReportAppApplication.class, args);
	}

}
