package com.aws.consumer.configuration;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;

import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementResultCallback;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;

import static org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties.UiService.LOGGER;

@Configuration
public class AmazonAWSConfiguration {

    @Value("${spring.cloud.aws.cloudwatch.region}")
    private String region;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonAWSConfiguration.class);

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return SqsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean(name = "defaultSqsListenerContainerFactory")
    public SqsMessageListenerContainerFactory<Object> defaultSqsMessageListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        // Use the builder to configure the factory
        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)  // Provide the async client
                .configure(options -> options
                        .acknowledgementMode(io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode.MANUAL)  // Set acknowledgment mode
                        .acknowledgementInterval(Duration.ofSeconds(5))  // Set acknowledgment interval (e.g., 5 seconds)
                        .acknowledgementThreshold(0))
                .acknowledgementResultCallback(new AckResultCallback()).sqsAsyncClient(sqsAsyncClient)// Set threshold for acknowledgments
                .build();  // Build the factory
    }

    static class AckResultCallback implements AcknowledgementResultCallback<Object> {

        @Override
        public void onSuccess(Collection<Message<Object>> message) {
            // This method is called when the message is successfully acknowledged
            LOGGER.info("Acknowledged: " + OffsetDateTime.now());
        }

        @Override
        public void onFailure(Collection<Message<Object>> message, Throwable throwable) {
            // This method is called if there's an issue and the message is rejected
            LOGGER.error("Failed to process message: " + throwable);
        }
    }
}
