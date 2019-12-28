//package com.example.demo.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaSender {
//
//    String kafkaTopic = "java_in_use_topic";
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    public void send(String message) {
//        kafkaTemplate.send(kafkaTopic, message);
//    }
//}
