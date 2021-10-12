
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import com.advintic.integrator.scheduled.WorklistScheduled;
import com.os.api.dicom.DicomUtility;
import com.pixelmed.dicom.DicomException;
import java.io.File;
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
public class Test {

    public static void main(String[] args) throws HL7Exception, IOException, DicomException, LLPException {
//                String createRadiologyOrderMessage = MessageBuilder.createRadiologyOrderMessage(ormMessageContent);
//        System.out.println(createRadiologyOrderMessage);
/*
        MessageContent ormMessageContent = new MessageContent(new Date(), "PatientID", new Date("12/04/1988"), "AHMED", "patientNationalId",
        "M", "123456", "examName2", "CR", "worklistStatus", "examCompleted");
        Message createRadiologyOrderMessage1 = MessageBuilder.createRadiologyOrderMessage(ormMessageContent,"N");
        //        Message createRadiologyOrderMessage1 = MessageBuilder.createPatientEditMessage(ormMessageContent);
        String toString = createRadiologyOrderMessage1.toString();
        System.out.println("#################################################################");
        System.out.println(toString);
        System.out.println("#################################################################");
        HL7Utils.sendHL7Message("advintic-ops.ddns.net", 3332, createRadiologyOrderMessage1);
        System.out.println(new Date("2019/02/14 00:00:00").getTime());
         */
        File mppsFile = new File("C:\\AdvinticProjects\\integrator\\MPPS\\1.2.826.0.1.3680043.2.1545.1.2.1.7.20200116.130510.247.51");
        String dicomTagValue = DicomUtility.getDicomTagValue(mppsFile, "AccessionNumber");
        System.out.println("AccessionNumber " + dicomTagValue);

        
    }

}
