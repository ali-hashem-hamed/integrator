package com.advintic.integrator.module.viewer;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class StudyWrapper implements Serializable {
    private String patientName;
    private String patientAge;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date studyDate;
    private String refDoctor;
    private String examName;
    private String userId;
    private String patientId;
    private String patientBrithDate;
    private String studyId;
    private String examId;

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public StudyWrapper() {
    }

    public StudyWrapper(String patientName, String patientAge, Date studyDate, String refDoctor, String examName, String userId, String patientId, String patientBrithDate, String studyId) {

        this.patientName = patientName;
        this.patientAge = patientAge;
        this.studyDate = studyDate;
        this.refDoctor = refDoctor;
        this.examName = examName;
        this.userId = userId;
        this.patientId = patientId;
        this.patientBrithDate = patientBrithDate;
        this.studyId = studyId;
    }


    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    public String getRefDoctor() {
        return refDoctor;
    }

    public void setRefDoctor(String refDoctor) {
        this.refDoctor = refDoctor;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientBrithDate() {
        return patientBrithDate;
    }

    public void setPatientBrithDate(String patientBrithDate) {
        this.patientBrithDate = patientBrithDate;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }
}
