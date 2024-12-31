package com.example.task2.entities;

import java.time.LocalDate;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "term")
public class Term {

    @PrimaryKey(autoGenerate = true)
    private int termId;
    private String termName;
    private LocalDate startDate;
    private LocalDate endDate;

    private int courseId;
    private String courseName;




    public Term(int termId, String termName, LocalDate startDate, LocalDate endDate, String courseName, int courseId) {
        this.termId = termId;
        this.termName = termName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseName = courseName;
        this.courseId = courseId;
    }
    @Ignore
    public Term(int termId, String termName){
        this.termId = termId;
        this.termName = termName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
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
    public String toString(){
        return termName + "(" + startDate +"to " + endDate+ ")";
    }

}