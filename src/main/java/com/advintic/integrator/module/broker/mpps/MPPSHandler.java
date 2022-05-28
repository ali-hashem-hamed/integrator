
package com.advintic.integrator.module.broker.mpps;

import com.advintic.integrator.common.RequestHandler;
import com.advintic.integrator.module.yanbu.WorklistDao;
import com.advintic.integrator.module.yanbu.Worklist;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author mohamed
 */
@Component
@PropertySource("file:application.properties")
public class MPPSHandler {

    @Autowired
    WorklistDao worklistDao;

    @Value("${yanbu.lisenter.db.activate}")
    boolean dbLisenterActivated;

    @Value("${carev.integration.mpps.url}")
    String carevIntegrationUrl;

    @Value("${carev.integration.mpps.key}")
    String carevIntegrationKey;

    @Value("${carev.integration.mpps.secret}")
    String carevIntegrationSecret;


    @Autowired
    RequestHandler requestHandler;

    public void handleInProgress(String accessionNumber) {
       
        
        IntegrationWrapper w = new IntegrationWrapper();
        w.setExamination_id(accessionNumber);
        w.setStatus(IntegrationWrapper.IN_PROGRESS);
        requestHandler.post(carevIntegrationUrl , createRequestHeader(), w);

    }
    
    public void handleCompleted(String accessionNumber) {
        if (dbLisenterActivated) {
            Worklist findByAccessionNumber = worklistDao.findByAccessionNumber(accessionNumber);
            worklistDao.setExamCompleted(findByAccessionNumber.getId());
            worklistDao.setHandled(findByAccessionNumber.getId(), 0);
        }
        
        IntegrationWrapper w = new IntegrationWrapper();
        w.setExamination_id(accessionNumber);
        w.setStatus(IntegrationWrapper.COMPLETED);
        requestHandler.post(carevIntegrationUrl , createRequestHeader(), w);

    }

    private HttpHeaders createRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("Authorization"+ "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        headers.add("Authorization", "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        return headers;

    }



}
