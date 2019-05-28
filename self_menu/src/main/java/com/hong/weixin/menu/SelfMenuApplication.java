package com.hong.weixin.menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import com.hong.commons.config.EventListenerConfig;
import com.hong.commons.domain.event.EventInMessage;

@SpringBootApplication
@EntityScan("com.hong")
@ComponentScan("com.hong")
public class SelfMenuApplication implements EventListenerConfig {
	
	public static void main(String[] args) {
		SpringApplication.run(SelfMenuApplication.class, args);
	}

	@Override
	public void handle(EventInMessage msg) {
		
	}
}
