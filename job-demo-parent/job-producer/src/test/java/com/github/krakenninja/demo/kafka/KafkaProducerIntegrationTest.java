package com.github.krakenninja.demo.kafka;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * Make sure your system environment has the following KV : 
 * <ul>
 *     <li>{@code export KAFKA_BOOTSTRAP_SERVERS="pkc-rgm37.us-west-2.aws.confluent.cloud:9092"}</li>
 *     <li>{@code export KAFKA_SECURITY_PROTOCOL="SASL_SSL"}</li>
 *     <li>{@code export KAFKA_SASL_MECHANISM="PLAIN"}</li>
 *     <li>{@code export KAFKA_SASL_JAAS_CONFIG="org.apache.kafka.common.security.plain.PlainLoginModule required username='<YOUR-CONFLUENT-KAFKA-API-KEY>' password='<YOUR-CONFLUENT-KAFKA-API-SECRET>';"}</li>
 * </ul>
 * @author Christopher CKW
 */
@Slf4j
@SpringBootTest
@ExtendWith(
    MockitoExtension.class
)
public class KafkaProducerIntegrationTest
{
    private final String KAFKA_TOPIC_NAME = "job-demo-topic";
    
    private final String KAFKA_TOPIC_MESSAGE_KEY = "job-demo-topic-message";
    
    private final String KAFKA_TOPIC_MESSAGE_VALUE = "Hello World!";
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeAll
    public static void setUpClass()
    {
        log.info(
            "\n\nSystem environment details (Kafka) --- {}\n\n\n",
            System.getenv().entrySet().stream().filter(
                e -> e.getKey().toLowerCase().startsWith(
                    "kafka"
                )
            ).map(
                e -> String.format(
                    "%n\t%s=%s",
                    e.getKey(),
                    e.getValue()
                )
            ).collect(
                Collectors.joining()
            )
        );
    }
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp() {}
    
    @AfterEach
    public void tearDown() {}

    @Test
    public void sendMessage() 
    {
        assertNotNull(
            kafkaTemplate
        );
        
        final CompletableFuture<SendResult<String, String>> kafkaSendResult = kafkaTemplate.send(
            KAFKA_TOPIC_NAME,
            KAFKA_TOPIC_MESSAGE_KEY,
            KAFKA_TOPIC_MESSAGE_VALUE
        );
        
        assertNotNull(
            kafkaSendResult
        );
        
        final CompletableFuture<String> kafkaSendResultMessage = kafkaSendResult.handle(
            (result, exception) -> {
                if(Objects.nonNull(
                    exception
                ))
                {
                    exception.printStackTrace(
                        System.err
                    );
                    fail(
                        String.format(
                            "Send Kafka string message K='%s' | V='%s' to topic '%s' FAILURE ; %s",
                            KAFKA_TOPIC_MESSAGE_KEY,
                            KAFKA_TOPIC_MESSAGE_VALUE,
                            KAFKA_TOPIC_NAME,
                            exception.getMessage()
                        )
                    );
                    return null;
                }
                else
                {
                    final ProducerRecord<String, String> kafkaProducerRecord = result.getProducerRecord();
                    assertNotNull(
                        kafkaProducerRecord
                    );
                    assertNotNull(
                        kafkaProducerRecord.key()
                    );
                    assertEquals(
                        KAFKA_TOPIC_MESSAGE_KEY,
                        kafkaProducerRecord.key()
                    );
                    return kafkaProducerRecord.value();
                }
            }
        );
        
        final String sentMessageValue = kafkaSendResultMessage.join();
        assertNotNull(
            sentMessageValue
        );
        assertEquals(
            KAFKA_TOPIC_MESSAGE_VALUE,
            sentMessageValue
        );
    }
}
