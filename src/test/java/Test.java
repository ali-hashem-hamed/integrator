
import com.advintic.integrator.hl7.ORMMessageContent;
import com.advintic.integrator.hl7.ORMO01MessageBuilder;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ADT_A04;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import com.advintic.integrator.hl7.HL7Utils;
import com.pixelmed.dicom.DicomException;
import java.io.IOException;
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
public class Test {

    public static void main(String[] args) throws HL7Exception, IOException, DicomException, LLPException {
        //        String createRadiologyOrderMessage = ORMO01MessageBuilder.createRadiologyOrderMessage(ormMessageContent);
//        System.out.println(createRadiologyOrderMessage);

//        ORMMessageContent ormMessageContent = new ORMMessageContent((int) new Date().getTime(), "patientid00", new Date("12/04/1966"), "patientFullName", "patientNationalId",
//                "M", "accessionNumber4444444444444", "examName", "modalityName", "worklistStatus", "examCompleted");
//
//        Message createRadiologyOrderMessage1 = ORMO01MessageBuilder.createRadiologyOrderMessage(ormMessageContent);
//        String toString = createRadiologyOrderMessage1.toString();
//        System.out.println(toString);
//        HL7Utils.sendHL7Message("advintic-ops.ddns.net", 3332, createRadiologyOrderMessage1);

//        System.out.println(new Date("2019/02/14 00:00:00").getTime());

    }

}
