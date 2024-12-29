package com.aws.producer;

import com.aws.producer.model.Product;
import com.aws.producer.service.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {
	@Autowired
	private MessageSender messageSender;

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Product product = new Product("1","Krishnan","5000000");

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(product);
		messageSender.send(json);
	}



}
