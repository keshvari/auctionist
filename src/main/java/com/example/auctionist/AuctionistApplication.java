package com.example.auctionist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class AuctionistApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionistApplication.class, args);
	}

}
