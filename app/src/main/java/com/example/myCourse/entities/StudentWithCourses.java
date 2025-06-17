package com.example.myCourse.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import java.util.List;

public class StudentWithCourses {
    @Embedded public Student student;

    @Relation(
            parentColumn  = "studentId",
            entityColumn  = "courseId",
            associateBy   = @Junction(StudentCourseCrossRef.class)
    )
    public List<Course> courses;
}
