package com.advintic.integrator.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.EVN;
import ca.uhn.hl7v2.model.v25.segment.IPC;
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
public class ORMO01MessageBuilder {

    public static Message createRadiologyOrderMessage(ORMMessageContent messageContent) throws HL7Exception {
        ORM_O01 message = new ORM_O01();

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
//        pv1.getAssignedPatientLocation().getPointOfCare().setValue("PointOfCare");
//        pv1.getAssignedPatientLocation().getPersonLocationType().setValue("PersonLocationType");
//        pv1.getReferringDoctor(0).getIDNumber().setValue("1");
//        pv1.getReferringDoctor(0).getFamilyName().getSurname().setValue("ReferringDoctorFNAME");
//        pv1.getReferringDoctor(0).getGivenName().setValue("ReferringDoctorNAME");

        // handle ORC component
        ORC orc = message.getORDER().getORC();
//        orc.getPlacerOrderNumber().getEntityIdentifier().setValue("PlacerOrderNumber54545");
//        orc.getFillerOrderNumber().getEntityIdentifier().setValue("FillerOrderNumber54512");
        orc.getEnteredBy(0).getGivenName().setValue("EnteredByADV");
        orc.getOrderControl().setValue("NW");
        orc.getQuantityTiming(0).getStartDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(messageContent.getCreationDateTime()));

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
        obr.getPlacerField1().setValue(messageContent.getAccessionNumber());
        obr.getDiagnosticServSectID().setValue(messageContent.getModalityName());
        obr.getPlacerField2().setValue(messageContent.getAccessionNumber());////////////ReqProcId       
//        obr.getFillerField1().setValue("ScheduledProcedureStepID");
//        obr.getSpecimenSource().getBodySite().getAlternateIdentifier().setValue("getAlternateIdentifier");
//        obr.getSpecimenSource().getBodySite().getAlternateText().setValue("getAlternateText");
//        obr.getObr13_RelevantClinicalInformation().setValue("MedicalAlerts"); 

//        obr.getObr15_SpecimenSource().getSiteModifier().getText().setValue("getSiteModifier");
        // note that we are just sending modality here, not the device location
//        obr.getQuantityTiming(0).getPriority().setValue("STAT");
        // break the reason for study up by lines
//        obr.getReasonForStudy(0).getText().setValue("Creating a test order programmatically");
//        obr.getReasonForStudy(1).getText().setValue("This is a test order. Please ignore this order.");
        // handle ZDS Segment
        message.addNonstandardSegment("ZDS");
        Terser t = new Terser(message);
        t.set("ZDS-1-1", HL7Utils.generateSUID());
        return message;
    }

}
