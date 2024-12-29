package com.aws.consumer.service;

import com.aws.consumer.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class MessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);
    private final String queueName = "CivilQueue";

    @SqsListener("CivilQueue")
    public void listen(Message<?> message) throws JsonProcessingException, JsonMappingException {
        String payload = message.getPayload().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(payload, Product.class);

        System.out.println("Received product: " + product);
        LOGGER.info(message.getPayload().toString()+"received on listener at {}", OffsetDateTime.now());
        Acknowledgement.acknowledge(message);
    }




}
