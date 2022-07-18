package com.advintic.integrator.module.irc;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/calim")
public class CalimService {
    @Autowired
    ClaimDao claimDao;

    @RequestMapping(value = "/list",
            method = RequestMethod.GET ,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Claim> list(){
        return  (List<Claim>) claimDao.findAll();

    }
}
