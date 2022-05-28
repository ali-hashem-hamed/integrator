/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advintic.integrator.module.irc;


import com.advintic.integrator.common.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Mahmoud
 */
@Component
@PropertySource("file:application.properties")

public class ClaimScheduled {



    @Value("${irc.lisenter.db.activate}")
    boolean dbTabelListenerActivated;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    ClaimDao claimDao;

    @Value("${carev.integration.claim.url}")
    String carevIntegrationUrl;

    @Value("${carev.integration.claim.key}")
    String carevIntegrationKey;

    @Value("${carev.integration.claim.secret}")
    String carevIntegrationSecret;

    @Autowired
    RequestHandler requestHandler;

    private HttpHeaders createRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("Authorization"+ "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        headers.add("Authorization", "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        return headers;

    }


    @Scheduled(fixedRate = 1000 * 60)
    public void searchForUnhandledClaim() throws JsonProcessingException {
        
        if(!dbTabelListenerActivated) return;
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("Calims Started " + dateTimeFormatter.format(LocalDateTime.now()));
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        ObjectMapper mapper = new ObjectMapper();
        List<Claim> unhandledClaim = (List<Claim>) claimDao.findAll();
        for (Claim claim : unhandledClaim) {
            System.out.println("calim Object" + mapper.writeValueAsString(claim));
            requestHandler.post(carevIntegrationUrl , createRequestHeader() , claim);







        }
    }

}
