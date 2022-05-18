package com.advintic.integrator.module.broker.services;

import com.advintic.integrator.module.broker.hl7.HL7Utils;
import com.advintic.integrator.module.broker.hl7.MessageBuilder;
import com.advintic.integrator.module.broker.hl7.MessageContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@PropertySource("file:application.properties")
@Service
@RequestMapping("/hl7")
public class Hl7Services {

    @Value("${hl7.host}")
    String HL7Host;
    @Value("${hl7.port}")
    Integer HL7Port;




    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            value = "/sendMessage")

    public ResponseEntity<String> sendHl7Message(@RequestBody MessageContent messageContent) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("-----------------------------------");
        System.out.println("NEW Message Recieved : ");
        System.out.println(mapper.writeValueAsString(messageContent));
        System.out.println("----------------------------------");

              if(HL7Utils.sendHL7Message(HL7Host , HL7Port ,
              MessageBuilder.createRadiologyOrderMessage(messageContent))){
                  return new ResponseEntity<>("sent", HttpStatus.OK);
              } else throw new Exception("Unable to send Message");



    }
}
