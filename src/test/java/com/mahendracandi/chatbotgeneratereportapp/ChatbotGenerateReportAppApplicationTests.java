package com.mahendracandi.chatbotgeneratereportapp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatbotGenerateReportAppApplicationTests {

	@Test
	void contextLoads() {
		String utc1 = "2020-09-01T00:36:47.280Z";
		String utc2 = "2020-09-01T00:38:54.928Z";
		String utc3 = "2020-09-01T02:00:03.852Z";
		
		//01-09-2020 07:36:35
		
		//2020-08-31T19:56:12.304Z
		//01-09-2020 02:56:12
		
//		LocalDateTime dt = LocalDateTime.parse(utc, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		
//		convertToWIB(utc1);
//		System.out.println("----------");
//		convertToWIB(utc2);
//		System.out.println("----------");
//		convertToWIB(utc3);
		
		
	}
	
	private void convertToWIB(String value) {
		Instant instant = Instant.parse(value);
		System.out.println(instant);
		ZonedDateTime zoneDateTime = instant.atZone(ZoneId.systemDefault());
		System.out.println(zoneDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
	}
	

}
