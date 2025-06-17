package com.example.myCourse.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.myCourse.entities.StudentCourseCrossRef;

import java.util.List;

@Dao
public interface StudentCourseCrossRefDAO {
    @Query("SELECT * FROM student_course_cross_ref")
    List<StudentCourseCrossRef> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudentCourseCrossRef crossRef);

    @Delete
    void delete(StudentCourseCrossRef crossRef);
}