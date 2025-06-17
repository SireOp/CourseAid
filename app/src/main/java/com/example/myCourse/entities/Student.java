package com.example.myCourse.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Embedded;
import androidx.room.Relation;
import androidx.room.Junction;
import java.util.List;

/**
 * Represents a student without any direct course foreign key.
 * Many-to-many relationship with Course is handled via the cross-ref entity.
 */
@Entity(tableName = "student")
public class Student {

    private transient String startDateString;
    private transient String endDateString;

    @PrimaryKey(autoGenerate = true)
    private Integer studentId;

    private String studentName;
    private String address;
    private String phone;
    private String grade;

    // Constructor without courseId
    public Student(Integer studentId, String studentName, String address, String phone, String grade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.address = address;
        this.phone = phone;
        this.grade = grade;
    }

    // Getters and setters
    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return getStudentName() + " - " + getGrade();
    }
}

