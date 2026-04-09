package com.matheushrs.psp_void_tools_orch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PspVoidToolsOrchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PspVoidToolsOrchApplication.class, args);
	}

}
