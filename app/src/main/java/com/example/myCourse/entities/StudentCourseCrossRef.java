package com.example.myCourse.entities;

import androidx.room.Entity;

@Entity(
        primaryKeys = {"studentId", "courseId"},
        tableName   = "student_course_cross_ref"
)
public class StudentCourseCrossRef {
    public int studentId;
    public int courseId;

    public StudentCourseCrossRef(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }
}