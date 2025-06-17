package com.example.myCourse.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "instructor")

public class Instructor {

    @PrimaryKey(autoGenerate = true)
    private Integer instructorId;
    private String name;

    private String phone;

    private String email;

    public Instructor(Integer instructorId, String name, String phone, String email) {
        this.instructorId = instructorId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public void setName(String name) {
        this.name = name;
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
}
