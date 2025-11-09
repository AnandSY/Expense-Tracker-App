package org.example.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.example.eventProducer.UserInfoEvent;
import org.example.model.UserInfoDto;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        byte[] retRes = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            retRes = objectMapper.writeValueAsString(userInfoEvent).getBytes();
        }catch (Exception e){
            e.printStackTrace();
        }
        return retRes;
    }

    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoEvent data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
