package com.advintic.integrator.hl7;

import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.EVN;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.util.Terser;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mahmoud
 */
public class MessageBuilder {

    public static final String NEW_WORKLIST = "NW";
    public static final String SCHEDULED_WORKLIST = "XO(SC)";
    public static final String STARTED_WORKLIST = "SC(IP)";
    public static final String COMPLETED_WORKLIST = "SC(CM)";
    public static final String DISCONTINUED_WORKLIST = "SC(DC)";
    public static final String CANCEL_WORKLIST = "SC(CA)";

    public static Message createRadiologyOrderMessage(MessageContent messageContent, String status) {
        ORM_O01 message = new ORM_O01();
        try {
            // handle the MSH component
            MSH msh = message.getMSH();
            msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_1");
            HL7Utils.populateMessageHeader(msh, messageContent.getCreationDateTime(), "ORM", "O01", "ADV MSH");

            // handle the patient PID component
            ORM_O01_PATIENT patient = message.getPATIENT();
            PID pid = patient.getPID();
            pid.getPatientIdentifierList(0).getIDNumber().setValue(messageContent.getPatientId());
            pid.getPatientName(0).getFamilyName().getSurname().setValue(messageContent.getPatientFullName());//PatientFNAME
            // pid.getPatientName(0).getGivenName().setValue(messageContent.getPatientFullName());
            pid.getDateTimeOfBirth().getTime().setValue(HL7Utils.getHl7DateFormat().format(messageContent.getPatientBirthdate()));
            pid.getAdministrativeSex().setValue(messageContent.getPatientSex());

            // handle patient visit component
            PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();

            // handle ORC component
            ORC orc = message.getORDER().getORC();
            orc.getEnteredBy(0).getGivenName().setValue("EnteredByADV");
            orc.getOrderControl().setValue(status);

            orc.getQuantityTiming(0).getStartDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(messageContent.getCreationDateTime()));
            // handle OBR component
            OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
            obr.getObr20_FillerField1().setValue(messageContent.getAccessionNumber());
            obr.getPlacerField1().setValue(messageContent.getAccessionNumber());
            obr.getDiagnosticServSectID().setValue(messageContent.getModalityName());
            obr.getPlacerField2().setValue(messageContent.getAccessionNumber());////////////ReqProcId       
            message.addNonstandardSegment("ZDS");
            Terser t = new Terser(message);
            t.set("ZDS-1-1", HL7Utils.generateSUID());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    public static AbstractMessage createPatientEditMessage(MessageContent messageContent) {
        ADT_A01 message = new ADT_A01();
        try {
            // handle the MSH component
            MSH msh = message.getMSH();
            msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_2");
            HL7Utils.populateMessageHeader(msh, new Date(), "ADT", "A08", "ADV MSH");

            EVN evn = message.getEVN();
            evn.getEventTypeCode().setValue("A08");
            evn.getRecordedDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(new Date()));

            // handle the patient PID component
            PID pid = message.getPID();
            pid.getPatientIdentifierList(0).getIDNumber().setValue(messageContent.getPatientId());
            pid.getPatientName(0).getFamilyName().getSurname().setValue(messageContent.getPatientFullName());//PatientFNAME
            // pid.getPatientName(0).getGivenName().setValue(messageContent.getPatientFullName());
            pid.getDateTimeOfBirth().getTime().setValue(HL7Utils.getHl7DateFormat().format(messageContent.getPatientBirthdate()));
            pid.getAdministrativeSex().setValue(messageContent.getPatientSex());

            PV1 pv1 = message.getPV1();
            pv1.getPv12_PatientClass().setValue("O");

            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
}
