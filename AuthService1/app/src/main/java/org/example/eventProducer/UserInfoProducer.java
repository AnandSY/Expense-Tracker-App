package org.example.eventProducer;

import org.example.model.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.*;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.*;

@Service
public class UserInfoProducer {

    private final KafkaTemplate<String, UserInfoDto> kafkaTemplate;

    @Value("${spring.kafka.topic-json.name}")
    private String TOPIC_NAME;

    @Autowired
    public UserInfoProducer(KafkaTemplate<String, UserInfoDto> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventToKafka(UserInfoEvent userInfoEvent){
        Message<UserInfoEvent> message = MessageBuilder.withPayload(userInfoEvent).setHeader(KafkaHeaders.TOPIC, TOPIC_NAME).build();
        kafkaTemplate.send(message);
    }
}
