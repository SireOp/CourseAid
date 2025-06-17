package com.example.myCourse.entities;

import java.time.LocalDate;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "term")
public class Term {

    private transient String startDateString;

    private transient String endDateString;

    @PrimaryKey(autoGenerate = true)
    private Integer termId;
    private String termName;
    private LocalDate startDate;
    private LocalDate endDate;


    public Term(Integer termId, String termName, LocalDate startDate, LocalDate endDate) {
        this.termId = termId;
        this.termName = termName;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    @Ignore
    public Term(Integer termId, String termName) {
        this.termId = termId;
        this.termName = termName;
    }


    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
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

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public String toString() {
        return termName + "(" + startDate + "to " + endDate + ")";
    }

}