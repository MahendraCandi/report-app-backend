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
		String utc = "2020-06-30T17:03:36.481Z";
		
		LocalDateTime dt = LocalDateTime.parse(utc, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		Instant instant = Instant.parse(utc);
		System.out.println(instant);
//		ZonedDateTime zoneDateTime = instant.atZone(ZoneId.of("Asia/Jakarta"));
		ZonedDateTime zoneDateTime = instant.atZone(ZoneId.systemDefault());
		System.out.println(zoneDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		
	}

}
