package com.advintic.integrator.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
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
    public static final String CHANGE_WORKLIST = "XO";
    public static final String CANCEL_WORKLIST = "CA";
    public static final String STATUS_CHANGE_WORKLIST = "SC";


    public static final String SCHEDULED_WORKLIST = "SC";
    public static final String STARTED_WORKLIST = "IP";

    public static final String COMPLETED_WORKLIST = "CM";
    public static final String DISCONTINUED_WORKLIST = "DC";
    public static final String ARRIVED_WORKLIST = "AR";
   // public static final String CANCEL_WORKLIST = "CA";

    private final static String SCHEDULED = "SCHEDULED";
    private final static String INPROGRESS = "INPROGRESS";
    private final static String COMPLETED = "COMPLETED";
    private final static String CANCELLED = "CANCELLED";

    public static final String CREATE_WORKLIST_ACTION = "N";
    public static final String UPDATE_WORKLIST_ACTION = "U";
    public static final String CANCEL_WORKLIST_ACTION = "C";
    public static final String CHANGE_STATUS_WORKLIST_ACTION = "CS";


    public static Message createRadiologyOrderMessage(MessageContent messageContent) {
        HapiContext context = new DefaultHapiContext();
        ORM_O01 message = new ORM_O01();
        message.setParser(context.getPipeParser());
        try {
            // handle the MSH component
            MSH msh = message.getMSH();
            msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_1");
            HL7Utils.populateMessageHeader(msh, messageContent.getCreationDateTime(), "ORM", "O01", "ADV MSH");
            message.getNTE().getComment(0).setValue(messageContent.getComments());

            // handle the patient PID component
            ORM_O01_PATIENT patient = message.getPATIENT();
            PID pid = patient.getPID();
            pid.getPatientIdentifierList(0).getIDNumber().setValue(messageContent.getPatientId());
           // pid.getPatientName(0).getFamilyName().getSurname().setValue(messageContent.getPatientFullName());//PatientFNAME
             String[] nameComps = messageContent.getPatientFullName().split(" ");
             String familyName = "" ;
             String givenName =nameComps[0];
             for (int i=0; i<nameComps.length ; i++) {
                 if (i!=0 && i==(nameComps.length - 1)) {
                     familyName = nameComps[i];
                     break;
                 }
                 if(i>0) {
                     givenName += " " + nameComps[i];
                 }

             }
            System.out.println("NAME --------------------------------------------------");
            System.out.println(familyName);
            System.out.println(givenName);
            pid.getPatientName(0).getGivenName().setValue(givenName);
             pid.getPatientName(0).getFamilyName().getSurname().setValue(familyName);
            pid.getDateTimeOfBirth().getTime().setValue(HL7Utils.getHl7DateFormat().format(messageContent.getPatientBirthdate()));
            pid.getAdministrativeSex().setValue(messageContent.getPatientSex());

            // handle patient visit component
            PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();
            patient.getAL1().getAl13_AllergenCodeMnemonicDescription().getText().setValue(messageContent.getContrastAllergy());
            System.out.println("messageContent.Comment:: " + messageContent.getComments());

            pv1.getPv115_AmbulatoryStatus(0).setValue(messageContent.getPatientPregnant()==1?"B6":"");
                    //.setValue("000"+messageContent.getPatientPregnant());
            pv1.getPv18_ReferringDoctor(0).getIDNumber().setValue("1");
            pv1.getReferringDoctor(0).getXcn3_GivenName().setValue(messageContent.getPhysician());

            String status = null;
            if(messageContent.getWorklistStatus()!=null) {
                switch (messageContent.getWorklistStatus()) {
                    case SCHEDULED:
                        status = MessageBuilder.SCHEDULED_WORKLIST;
                        break;
                    case INPROGRESS:
                        status = MessageBuilder.STARTED_WORKLIST;
                        break;
                    case COMPLETED:
                        status = MessageBuilder.COMPLETED_WORKLIST;
                        break;

                    case CANCELLED:
                        status = MessageBuilder.CANCEL_WORKLIST;
                        break;

                }
            }
            if(status==null){
                throw  new Exception("Invalid Status: Accepted Values {SCHEDULED,INPROGRESS,COMPLETED,CANCELLED}");
            }



            // handle ORC component
            ORC orc = message.getORDER().getORC();
            orc.getOrc5_OrderStatus().setValue(status);
            String action = NEW_WORKLIST;
            if(messageContent.getAction()!=null) {
                switch (messageContent.getAction()) {

                    case UPDATE_WORKLIST_ACTION:
                        action = CHANGE_WORKLIST;
                        break;
                    case CANCEL_WORKLIST_ACTION:
                        action = CANCEL_WORKLIST;
                        break;
                    case CHANGE_STATUS_WORKLIST_ACTION:
                        action = STATUS_CHANGE_WORKLIST;
                        break;

                }
            }
            

            orc.getEnteredBy(0).getGivenName().setValue("EnteredByADV");
            orc.getOrderControl().setValue(action);
            orc.getOrc12_OrderingProvider(0).getFamilyName().getSurname().setValue(messageContent.getPhysician());



            orc.getQuantityTiming(0).getStartDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(messageContent.getCreationDateTime()));
            // handle OBR component
            OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
            obr.getObr16_OrderingProvider(0).getFamilyName().getSurname().setValue(messageContent.getPhysician());

            obr.getObr20_FillerField1().setValue(messageContent.getAccessionNumber());
            obr.getPlacerField1().setValue(messageContent.getAccessionNumber());
            obr.getDiagnosticServSectID().setValue(messageContent.getModalityName());
            obr.getPlacerField2().setValue(messageContent.getAccessionNumber());////////////ReqProcId
            obr.getObr4_UniversalServiceIdentifier().getAlternateText().setValue(messageContent.getExamName());
            obr.getObr13_RelevantClinicalInformation().setValue(messageContent.getComments());

            message.addNonstandardSegment("ZDS");
            Terser t = new Terser(message);
            t.set("ZDS-1-1", HL7Utils.generateSUID(messageContent.getAccessionNumber()));

        } catch (Exception e) {
            e.printStackTrace();
        }
try {


    System.out.println("Printing Encoded Message:");
    System.out.println(message.printStructure());
} catch (Exception e){
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
