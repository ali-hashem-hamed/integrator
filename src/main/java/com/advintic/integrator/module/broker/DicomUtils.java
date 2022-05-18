package com.advintic.integrator.module.broker;

import com.os.api.dicom.DicomQuery;
import com.os.api.dicom.model.DicomQueryResult;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.tool.findscu.FindSCU;

import java.util.HashMap;
import java.util.List;

public class DicomUtils {
    public static String queryDICOM(String pacsHost,int pacsPort, String pacsAE , String level , int sequence  , HashMap<Integer,String> searchKey){

        try{
            DicomQuery dicomQuery = new DicomQuery(pacsHost, pacsPort, pacsAE);
            System.out.println("pacsHost :: " + pacsHost);
            System.out.println("pacsPort :: " + pacsPort);
            System.out.println("pacsAE :: " + pacsAE);



            dicomQuery.setReturnTag(Tag.StudyInstanceUID);



            System.out.println("sequence::" + sequence);

            if(sequence==0)

                searchKey.forEach((key,value) -> dicomQuery.addSearchKey(key , value));
            else{


                searchKey.forEach((key,value) -> {
                    dicomQuery.addSearchKey(sequence,key , value);
                    dicomQuery.setReturnTag(key);

                });
            }


            DicomQueryResult queryResult = dicomQuery.query("Query",
                    FindSCU.InformationModel.StudyRoot , level);
            List<Attributes> atts= queryResult.getDicomObjectList();
            System.out.println("atts Size :: " +atts.size());
            System.out.println("atts :: " +atts);
            String filePath ;
            String sopUID;
            String seriesUID;
            String patientId ;
            String studyUID = null;
            for (Attributes att :atts) {

                sopUID= att.getString(Tag.SOPInstanceUID);
                seriesUID= att.getString(Tag.SeriesInstanceUID);
                studyUID= att.getString(Tag.StudyInstanceUID);
                patientId = att.getString(Tag.PatientID);
                System.out.println("Moda: " + att.getString(Tag.Modality));
                return studyUID;
            }



        }catch(Exception e) {
            e.printStackTrace();

        }
        return null;

    }
}
