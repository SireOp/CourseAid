package com.example.myCourse.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import java.util.List;

public class CourseWithStudents {
    @Embedded public Course course;

    @Relation(
            parentColumn  = "courseId",
            entityColumn  = "studentId",
            associateBy   = @Junction(StudentCourseCrossRef.class)
    )
    public List<Student> students;
}
