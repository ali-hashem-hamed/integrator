/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advintic.integrator.scheduled;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;

import com.advintic.integrator.db.dao.WorklistDao;
import com.advintic.integrator.db.model.Worklist;
import com.advintic.integrator.hl7.HL7Utils;
import com.advintic.integrator.hl7.MessageContent;
import com.advintic.integrator.hl7.MessageBuilder;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mahmoud
 */
@Component
@PropertySource("file:application.properties")

public class WorklistScheduled {



    @Value("${hl7.host}")
    String HL7Host;
    @Value("${hl7.port}")
    String HL7Port;

    @Value("${integration.lisenter.db.activate}")
    boolean dbTabelListenerActivated;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Autowired
    WorklistDao worklistDao;

    @Scheduled(fixedRate = 1000 * 60)
    public void searchForUnhandledWorklist() throws HL7Exception, LLPException, IOException {
        if(!dbTabelListenerActivated) return;
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("searchForUnhandledWorklist Started " + dateTimeFormatter.format(LocalDateTime.now()));
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        List<Worklist> unhandledWorklist = worklistDao.findByHandled(0);
        for (Worklist worklist : unhandledWorklist) {
            System.out.println("worklist " + worklist.getId());

            MessageContent messageContent = new MessageContent(worklist.getCreationDateTime(), worklist.getPatientId(),
                    worklist.getPatientBirthdate(), worklist.getPatientFullName(), worklist.getPatientNationalId(),
                    worklist.getPatientSex(), worklist.getAccessionNumber(), worklist.getExamName(), worklist.getModalityName(),
                    worklist.getWorklistStatus());
            messageContent.setPatientPregnant(worklist.getPatientPregnant());
            messageContent.setComments(worklist.getComments());
            messageContent.setContrastAllergy(worklist.getContrastAllergy());
            messageContent.setPhysician(worklist.getPhysician());
            messageContent.setAction(worklist.getWorklistStatus());



                 if (HL7Utils.sendHL7Message(HL7Host, Integer.parseInt(HL7Port), MessageBuilder.createRadiologyOrderMessage(messageContent))) {
                     worklistDao.setHandled(worklist.getId(), 1);

             }
        }
    }

}
