package com.advintic.integrator.hl7;


import ca.uhn.hl7v2.HL7Exception;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.UIDGenerator;

import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mahmoud
 */
public class ORMO01StringMessageBuilder {

    public static String createRadiologyOrderMessage(MessageContent message) throws HL7Exception, IOException, DicomException {
        String creationDate = HL7Utils.getHl7DateFormat().format(message.getCreationDateTime());
        String patientBirthdate = HL7Utils.getHl7DateFormat().format(message.getPatientBirthdate());
        String strMessage = "MSH|^~\\&|OPTIRIS|OPTIMALSYSTEMS|ISITE|CENTERNAME|" + creationDate + "||ORM^O01|1576680435834|P|2.3\n"
                + "PID|1||" + message.getPatientId() + "||" + message.getPatientFullName() + "||" + patientBirthdate + "|" + message.getPatientSex() + "\n"
                + "PV1|1|O\n"
                + "ORC|NW||||S\n"
                + "OBR|1||2565|" + message.getExamName() + "||" + creationDate + "||||||||||refDoctor||"
                + message.getAccessionNumber() + "|"
                + message.getAccessionNumber() + "|" + message.getAccessionNumber() + "||||"
                + message.getModalityName() + "||||||||||||"
                + creationDate + "\n"
                + "ZDS|" + new UIDGenerator().getNewStudyInstanceUID("Study") + "^100^Application^DICOM";
        return strMessage;
    }

}
