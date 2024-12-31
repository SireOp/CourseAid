package com.example.task2.entities;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "course")

public class Course {
@PrimaryKey(autoGenerate = true)
private int courseId;

private String title;

private LocalDate startDate;

private LocalDate endDate;

private String status;

private String assessment;

private String notes;

private String courseInfo;

@ColumnInfo (name = "instructorId")
private Integer instructorId;

private String instructorName;

private String phone;

private String email;

//1:38 vid 3
private int termId;

    public Course(Integer courseId, String title, LocalDate startDate, LocalDate endDate, String status,String assessment, String notes, String courseInfo, Integer instructorId, String instructorName, String phone, String email, int termId) {
        this.courseId = courseId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.assessment = assessment;
        this.notes = notes;
        this.courseInfo = courseInfo;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.phone = phone;
        this.email = email;
        this.termId = termId;
    }
    @Ignore
    public Course(int courseId, String s, String o) {
        this.courseId = courseId;
        this.title = title;
        this.status = status;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssessment() {
        return assessment;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssessment(String assessment){return assessment;}

    public void setAssessment(String assessment){this.assessment = assessment;}

    public String getNotes(String notes){return notes;}

    public void setNotes(String notes){this.notes = notes;}


    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(String courseInfo) {
        this.courseInfo = courseInfo;
    }



}
