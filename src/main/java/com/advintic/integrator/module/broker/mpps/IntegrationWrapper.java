/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advintic.integrator.module.broker.mpps;

import java.io.Serializable;

/**
 *
 * @author mohamed
 */
public class IntegrationWrapper implements Serializable {
    
    public static final String COMPLETED = "COMPLETED";
    public static final String IN_PROGRESS = "INPROGRESS";
    
    
   private String examination_id;
   private String  status;

    public IntegrationWrapper(String examination_id, String status) {
        this.examination_id = examination_id;
        this.status = status;
    }

    public IntegrationWrapper() {
    }

    public String getExamination_id() {
        return examination_id;
    }

    public void setExamination_id(String examination_id) {
        this.examination_id = examination_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
   
   
   
    
}
