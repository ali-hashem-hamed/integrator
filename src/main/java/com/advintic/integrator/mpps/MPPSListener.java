/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advintic.integrator.mpps;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mahmoud
 */
@Component
@PropertySource("file:application.properties")
public class MPPSListener {

    @Value("${mpps.aet}")
    String MPPSAET;
    @Value("${mpps.port}")
    String MPPSPORT;
    
    @Autowired
    MPPSHandler handler;

    @EventListener(ApplicationReadyEvent.class)
    public void startMPPSListener() {
        String[] args2 = {"-b", MPPSAET + ":" + MPPSPORT, "-no-validate", "--directory", "MPPS"};
        MppsSCP.main(args2, handler);
    }

}
