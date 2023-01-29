package com.advintic.integrator.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestHandler {
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }
//studyuid
    public String post(String url, HttpHeaders headers, Object w) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("-----------------------------------------------------------------------------------------");
            System.out.println(mapper.writeValueAsString(w));
            System.out.println("---------------------------------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate = createRestTemplate();
        HttpEntity entity = new HttpEntity<>(w, headers);

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
            if(respEntity!=null)
                System.out.println("RESPONSE BODY " + respEntity.getBody());
        }
        return null;
    }
}
