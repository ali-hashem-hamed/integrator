package com.advintic.integrator.hl7;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
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
public class MessageContent implements Serializable {
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
     private Date creationDateTime;
     private String patientId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
     private Date patientBirthdate;
     private String patientFullName;
     private String patientNationalId;
     private String patientSex;
     private String accessionNumber;
     private String examName;
     private String modalityName;
     private String worklistStatus;
     private String comments;
     private String physician;
     private String contrastAllergy;
     private Integer patientPregnant;
     private String action;

    public MessageContent() {
    }

    public MessageContent(Date creationDateTime, String patientId, Date patientBirthdate, String patientFullName,
                          String patientNationalId, String patientSex, String accessionNumber, String examName, String modalityName,
                          String worklistStatus) {
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



    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPhysician() {
        return physician;
    }

    public void setPhysician(String physician) {
        this.physician = physician;
    }

    public String getContrastAllergy() {
        return contrastAllergy;
    }

    public void setContrastAllergy(String contrastAllergy) {
        this.contrastAllergy = contrastAllergy;
    }

    public Integer getPatientPregnant() {
        return patientPregnant;
    }

    public void setPatientPregnant(Integer patientPregnant) {
        this.patientPregnant = patientPregnant;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
