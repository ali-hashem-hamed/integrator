package com.advintic.integrator.module.viewer;

import com.advintic.integrator.common.RequestHandler;
import com.advintic.integrator.module.broker.mpps.IntegrationWrapper;
import com.os.api.oauth2.client.BasicServices;
import com.os.api.oauth2.client.TokenData;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@PropertySource("file:application.properties")
@RestController
@RequestMapping("/viewer/reportStatus")
public class Viewer extends BasicServices {
    @Value("${carev.integration.mpps.url}")
    String carevIntegrationUrl;


    @Value("${carev.integration.mpps.get_status.url}")
    String getStatusURl;
    @Value("${carev.integration.mpps.key}")
    String carevIntegrationKey;


    @Value("${carev.integration.mpps.secret}")
    String carevIntegrationSecret;
    final RequestHandler requestHandler;

    public Viewer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @GetMapping()
    public ResponseEntity<IntegrationWrapper> RetrieveStatus(@RequestParam("examination_id") String studyId) {
        try {
            RestTemplate restTemplate = requestHandler.createRestTemplate();
            String url = getStatusURl + "/?examination_id=" + studyId;
            HttpEntity<String> entity = new HttpEntity<>(createRequestHeader());
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            final ObjectMapper mapper = new ObjectMapper();
            final IntegrationWrapper integrationWrapper = mapper.convertValue(exchange.getBody().get("message"), IntegrationWrapper.class);
            return new ResponseEntity<>(integrationWrapper, exchange.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(new IntegrationWrapper(studyId, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }


    @PutMapping()
    public @ResponseBody ResponseEntity<IntegrationWrapper> updateReport(@RequestBody IntegrationWrapper reportWrapper) {
        try {
            RestTemplate restTemplate = requestHandler.createRestTemplate();
            HttpEntity entity = new HttpEntity<>(reportWrapper, createRequestHeader());
            ResponseEntity<Map> rep = restTemplate.exchange(carevIntegrationUrl, HttpMethod.POST, entity, Map.class);
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

    private HttpHeaders createRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("Authorization" + "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        headers.add("Authorization", "token " + carevIntegrationKey + ":" + carevIntegrationSecret);
        return headers;

    }

    @Override
    public void fetchNewTokenImp(String s) {

    }

    @Override
    public void assignNewTokenImp(TokenData tokenData) {

    }
}
