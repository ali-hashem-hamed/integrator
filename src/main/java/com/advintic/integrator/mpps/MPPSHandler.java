
package com.advintic.integrator.mpps;

import com.advintic.integrator.db.dao.WorklistDao;
import com.advintic.integrator.db.model.Worklist;
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

    @Value("${integration.lisenter.db.activate}")
    boolean dbLisenterActivated;

    @Value("${carev.integration.url}")
    String carevIntegrationUrl;

    @Value("${carev.integration.key}")
    String carevIntegrationKey;

    @Value("${carev.integration.secret}")
    String carevIntegrationSecret;

    public void handleInProgress(String accessionNumber) {
       
        
        IntegrationWrapper w = new IntegrationWrapper();
        w.setExamination_id(accessionNumber);
        w.setStatus(IntegrationWrapper.IN_PROGRESS);
        post(carevIntegrationUrl, w);

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
        post(carevIntegrationUrl, w);

    }

    private HttpHeaders createRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("Authorization"+ "token " + carevIntegrationKey + ":" + carevIntegrationSecret);

        headers.add("Authorization", "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        return headers;

    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

    private String post(String url, Object w) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("-----------------------------------------------------------------------------------------");
            System.out.println(mapper.writeValueAsString(w));
            System.out.println("---------------------------------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate = createRestTemplate();

        HttpEntity entity = new HttpEntity<>(w, createRequestHeader());

        ResponseEntity<String> respEntity = null;
        try {
            respEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (respEntity.getStatusCode() == HttpStatus.OK) {

//    ObjectMapper mapper = new ObjectMapper();
//    ViewResponseWrapper w = mapper.readValue(respEntity.getBody() , ViewResponseWrapper.class);
                System.out.println("respEntity Response" + respEntity.getBody());
                return respEntity.getBody();

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RESPONSE BODY " + respEntity.getBody());
        }
        return null;
    }

}
