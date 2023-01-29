package com.advintic.integrator.module.viewer;

import com.advintic.integrator.common.RequestHandler;
import com.advintic.integrator.module.broker.mpps.IntegrationWrapper;
import com.os.api.oauth2.client.BasicServices;
import com.os.api.oauth2.client.TokenData;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@PropertySource("file:application.properties")
@RestController
@RequestMapping("/viewer")
public class Viewer extends BasicServices {
    final RequestHandler requestHandler;
    //    Tele.v URLs
    @Value("${telev.integration.enabled}")
    String televIntegrationEnabled;
    @Value("${telev.integration.mpps.url}")
    String televIntegrationUrl;
    @Value("${telev.integration.mpps.getreportData.url}")
    String televRetrieveReportDataUrl;
    @Value("${telev.integration.mpps.get_status.url}")
    String televRetriveStatusURL;
    @Value("${telev.integration.mpps.key}")
    String televIntegrationKey;
    @Value("${telev.integration.mpps.secret}")
    String televIntegrationSecret;


    //Care.v URLs
    @Value("${carev.integration.mpps.url}")
    String carevIntegrationUrl;
    @Value("${carev.integration.mpps.getreportData.url}")
    String carvRetrieveReportDataUrl;
    @Value("${carev.integration.mpps.get_status.url}")
    String carvRetriveStatusURL;
    @Value("${carev.integration.mpps.key}")
    String carevIntegrationKey;
    @Value("${carev.integration.mpps.secret}")
    String carevIntegrationSecret;

    public Viewer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @GetMapping("/reportStatus")
    public ResponseEntity<IntegrationWrapper> RetrieveStatus(@RequestParam("examination_id") String studyId) {
        try {
            RestTemplate restTemplate = requestHandler.createRestTemplate();
            String url = carvRetriveStatusURL + "/?examination_id=" + studyId;
            HttpEntity<String> entity = new HttpEntity<>(carvRequestHeader());
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            final ObjectMapper mapper = new ObjectMapper();
            final IntegrationWrapper integrationWrapper = mapper.convertValue(exchange.getBody().get("message"), IntegrationWrapper.class);
            return new ResponseEntity<>(integrationWrapper, exchange.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(new IntegrationWrapper(studyId, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/reportStatus")
    public @ResponseBody ResponseEntity<IntegrationWrapper> updateReport(@RequestBody IntegrationWrapper reportWrapper) {
        String mppsUrl=carevIntegrationUrl;
        try {
            RestTemplate restTemplate = requestHandler.createRestTemplate();
            HttpEntity entity = new HttpEntity<>(reportWrapper, carvRequestHeader());
            ResponseEntity<Map> rep = restTemplate.exchange(mppsUrl, HttpMethod.POST, entity, Map.class);
            if (rep.getStatusCode() == HttpStatus.OK) return new ResponseEntity<>(reportWrapper, HttpStatus.ACCEPTED);
            else if (rep.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("Error in update Report status " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);

        }


    }

    @GetMapping("/reportData")
    private @ResponseBody ResponseEntity<StudyWrapper> retrieveReportData(@RequestParam("examination_id") String exam_id) {
        try {
            //            ?exam_id=HLC-CPR-2023-00029
            RestTemplate restTemplate = requestHandler.createRestTemplate();
            String url = carvRetrieveReportDataUrl + "/?exam_id=" + exam_id;
            HttpEntity<String> entity = new HttpEntity<>(carvRequestHeader());
            ResponseEntity<Map> rep = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            final ObjectMapper mapper = new ObjectMapper();
            final StudyWrapper studyWrapper = mapper.convertValue(rep.getBody().get("message"), StudyWrapper.class);
            return new ResponseEntity<>(studyWrapper, rep.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in retrieving Report Data = " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    private HttpHeaders carvRequestHeader() {
        return createRequestHeader(carevIntegrationKey, carevIntegrationSecret);

    }
    private HttpHeaders televRequestHeader() {
        return createRequestHeader(televIntegrationKey, televIntegrationSecret);

    }

    private HttpHeaders createRequestHeader(String IntegrationKey,String integrationSecret) {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("Authorization" + "token " + IntegrationKey + ":" + integrationSecret);
        headers.add("Authorization", "token " + IntegrationKey + ":" + integrationSecret);
        return headers;

    }

    @Override
    public void fetchNewTokenImp(String s) {

    }

    @Override
    public void assignNewTokenImp(TokenData tokenData) {

    }
}
