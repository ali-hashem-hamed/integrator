/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advintic.integrator.db.scheduled;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.Message;

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

    public static final String NEW_STATUS = "N";
    public static final String UPDATE_STATUS = "U";
    public static final String DELETE_STATUS = "D";

    @Value("${hl7.host}")
    String HL7Host;
    @Value("${hl7.port}")
    String HL7Port;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Autowired
    WorklistDao worklistDao;

    @Scheduled(fixedRate = 1000 * 60)
    public void searchForUnhandledWorklist() throws HL7Exception, LLPException, IOException {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("searchForUnhandledWorklist Started " + dateTimeFormatter.format(LocalDateTime.now()));
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        List<Worklist> unhandledWorklist = worklistDao.findByHandled(0);
        for (Worklist worklist : unhandledWorklist) {
            System.out.println("worklist " + worklist.getId());

            MessageContent messageContent = new MessageContent(worklist.getCreationDateTime(), worklist.getPatientId(),
                    worklist.getPatientBirthdate(), worklist.getPatientFullName(), worklist.getPatientNationalId(),
                    worklist.getPatientSex(), worklist.getAccessionNumber(), worklist.getExamName(), worklist.getModalityName(),
                    worklist.getWorklistStatus(), worklist.getExamCompleted());

            Message message = null;

            switch (worklist.getWorklistStatus().toUpperCase()) {
                case NEW_STATUS:
                    if (worklist.getExamCompleted().equalsIgnoreCase("1")) {
                        System.out.println("Exam Completed Status");
                        message = MessageBuilder.createRadiologyOrderMessage(messageContent, MessageBuilder.COMPLETED_WORKLIST);
                    } else {
                        System.out.println("New Status");
                        message = MessageBuilder.createRadiologyOrderMessage(messageContent, MessageBuilder.SCHEDULED_WORKLIST);
                    }
                    break;
                case UPDATE_STATUS:
                    System.out.println("Update Status");

                    message = MessageBuilder.createRadiologyOrderMessage(messageContent, MessageBuilder.CANCEL_WORKLIST);
                    HL7Utils.sendHL7Message(HL7Host, Integer.parseInt(HL7Port), message);

                    message = MessageBuilder.createRadiologyOrderMessage(messageContent, MessageBuilder.SCHEDULED_WORKLIST);
                    break;
                case DELETE_STATUS:
                    System.out.println("Delete Status");
                    message = MessageBuilder.createRadiologyOrderMessage(messageContent, MessageBuilder.CANCEL_WORKLIST);
                    break;
                default:
                    System.out.println("no match");
            }

            boolean sendHL7Message = HL7Utils.sendHL7Message(HL7Host, Integer.parseInt(HL7Port), message);
            if (sendHL7Message) {
                worklistDao.setHandled(worklist.getId(), 1);
            }
        }
    }

}
