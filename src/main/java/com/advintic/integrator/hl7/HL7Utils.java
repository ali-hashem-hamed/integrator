package com.advintic.integrator.hl7;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mahmoud
 */
import ca.uhn.hl7v2.*;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.v25.message.*;
import ca.uhn.hl7v2.model.v25.segment.*;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.idgenerator.UUIDGenerator;
import java.io.IOException;

import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dcm4che3.data.Tag;
import org.dcm4che3.util.UIDUtils;
import sun.awt.X11.XSystemTrayPeer;

public class HL7Utils {

    public static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static boolean sendHL7Message(String remoteHost, int remotePort, Message msg) {
        Connection newClientConnection = null;
        boolean applicationAccept=false;
        try {
            ConnectionHub connectionHub = ConnectionHub.getInstance();
            newClientConnection = connectionHub.attach(remoteHost, remotePort, new PipeParser(), MinLowerLayerProtocol.class);
            Initiator initiator = newClientConnection.getInitiator();
            Message response = initiator.sendAndReceive(msg);

 
//            System.out.print(response);
            PipeParser pipeParser = new PipeParser();
            String responseString = pipeParser.encode(response);
            System.out.println("Received response:\n" + responseString);
            applicationAccept=responseString.contains("MSA|AA|");
        } catch (Exception ex) {
            System.out.println("catch #############");
            ex.printStackTrace();
        } finally {
            if (newClientConnection != null) {
                newClientConnection.close();
            }
        }
        return applicationAccept;
    }

    public static MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent, String sendingFacility) throws DataTypeException {
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingFacility().getHd1_NamespaceID().setValue(sendingFacility);
        msh.getSendingFacility().getUniversalID().setValue(sendingFacility);
        msh.getSendingFacility().getNamespaceID().setValue(sendingFacility);
        msh.getDateTimeOfMessage().getTs1_Time().setValue(getHl7DateFormat().format(dateTime));
        msh.getMessageType().getMessageCode().setValue(messageType);
        msh.getMessageType().getTriggerEvent().setValue(triggerEvent);
        //  TODO: do we need to send Message Control ID?
        msh.getProcessingID().getProcessingID().setValue("P");  // stands for production (?)
        msh.getVersionID().getVersionID().setValue("2.5");

        return msh;
    }

    public static ORR_O02 generateACK(String messageControlId, String sendingFacility) throws DataTypeException {
        ORR_O02 ack = new ORR_O02();

        populateMessageHeader(ack.getMSH(), new Date(), "ORR", "O02", sendingFacility);

        ack.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AA.toString());
        ack.getMSA().getMessageControlID().setValue(messageControlId);

        return ack;
    }

    public static ACK generateErrorACK(String messageControlId, String sendingFacility, String errorMessage) throws DataTypeException {
        ACK ack = new ACK();

        populateMessageHeader(ack.getMSH(), new Date(), "ORR", "002", sendingFacility);

        ack.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AE.toString());
        ack.getMSA().getMessageControlID().setValue(messageControlId);
        ack.getMSA().getTextMessage().setValue(errorMessage);

        return ack;
    }

    public static String generateSUID(String accessionNumber) {
        String UID = UIDUtils.createNameBasedUID(accessionNumber.getBytes());
        System.out.println("UID :: " + UID);
        return UID;
    }


    public static void main(String[] args){
        MessageContent messageContent = new MessageContent();
        messageContent.setExamName("Exam name");
        messageContent.setCreationDateTime(new Date());
        messageContent.setModalityName("CT");
        messageContent.setPatientId("1");
        messageContent.setPatientFullName("Mohamed");
        messageContent.setAccessionNumber("5551");
        messageContent.setPatientBirthdate(new Date());
        messageContent.setPatientPregnant(1);
try {
            ObjectMapper mapper = new ObjectMapper();
            String str = mapper.writeValueAsString(messageContent);
            System.out.println(str);
        } catch (Exception e){

    e.printStackTrace();
}
        HL7Utils.sendHL7Message("stage.advintic.com" , 2575 ,
                MessageBuilder.createRadiologyOrderMessage(messageContent));

    }

}
