package com.advintic.integrator.module.broker.services;

import com.advintic.integrator.module.broker.DicomUtils;
import org.dcm4che3.data.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

@PropertySource("file:application.properties")
@Service
@RequestMapping("/view")
public class ViewerServices {


    @Value("${pacs.host}")
    String PACSHost;
    @Value("${pacs.port}")
    Integer PACSPort;

    @Value("${pacs.ae}")
    String PACSAe;

    @Value("${viewer.url}")
    String viewerURL;


    @RequestMapping(method = RequestMethod.GET)
    public RedirectView view(@RequestParam String accession){
        HashMap searchKeys = new HashMap();

        searchKeys.put(Tag.AccessionNumber, accession);
        String studyUID = DicomUtils.queryDICOM(PACSHost, PACSPort, PACSAe, "STUDY", 0, searchKeys);
        if (studyUID == null) {
            searchKeys = new HashMap();
            searchKeys.put(Tag.RequestedProcedureID, accession);
            studyUID = DicomUtils.queryDICOM(PACSHost, PACSPort, PACSAe, "SERIES", Tag.RequestAttributesSequence, searchKeys);
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(viewerURL+"/"+studyUID);
        return redirectView;
    }
}
