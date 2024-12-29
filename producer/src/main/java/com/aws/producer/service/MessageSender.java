package com.aws.producer.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
    @Autowired
    private SqsTemplate sqsTemplate;

    private final String queueName = "CivilQueue";

    public void send(String productJson) {
        sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(queueName).payload(productJson));
        System.out.println(productJson + " sent to " + queueName);
    }
}
