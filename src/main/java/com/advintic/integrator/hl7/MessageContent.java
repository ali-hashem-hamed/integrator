package com.advintic.integrator.hl7;


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
public class MessageContent {
     private Date creationDateTime;
     private String patientId;
     private Date patientBirthdate;
     private String patientFullName;
     private String patientNationalId;
     private String patientSex;
     private String accessionNumber;
     private String examName;
     private String modalityName;
     private String worklistStatus;
     private String examCompleted;

    public MessageContent() {
    }

    public MessageContent(Date creationDateTime, String patientId, Date patientBirthdate, String patientFullName, String patientNationalId, String patientSex, String accessionNumber, String examName, String modalityName, String worklistStatus, String examCompleted) {
        this.creationDateTime = creationDateTime;
        this.patientId = patientId;
        this.patientBirthdate = patientBirthdate;
        this.patientFullName = patientFullName;
        this.patientNationalId = patientNationalId;
        this.patientSex = patientSex;
        this.accessionNumber = accessionNumber;
        this.examName = examName;
        this.modalityName = modalityName;
        this.worklistStatus = worklistStatus;
        this.examCompleted = examCompleted;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Date getPatientBirthdate() {
        return patientBirthdate;
    }

    public void setPatientBirthdate(Date patientBirthdate) {
        this.patientBirthdate = patientBirthdate;
    }

    public String getPatientFullName() {
        return patientFullName;
    }

    public void setPatientFullName(String patientFullName) {
        this.patientFullName = patientFullName;
    }

    public String getPatientNationalId() {
        return patientNationalId;
    }

    public void setPatientNationalId(String patientNationalId) {
        this.patientNationalId = patientNationalId;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getModalityName() {
        return modalityName;
    }

    public void setModalityName(String modalityName) {
        this.modalityName = modalityName;
    }

    public String getWorklistStatus() {
        return worklistStatus;
    }

    public void setWorklistStatus(String worklistStatus) {
        this.worklistStatus = worklistStatus;
    }

    public String getExamCompleted() {
        return examCompleted;
    }

    public void setExamCompleted(String examCompleted) {
        this.examCompleted = examCompleted;
    }
     
     
}
