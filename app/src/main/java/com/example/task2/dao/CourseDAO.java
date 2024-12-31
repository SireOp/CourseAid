package com.example.task2.dao;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.task2.entities.Course;
import com.example.task2.entities.Instructor;
import com.example.task2.entities.Term;


import java.util.List;


@Dao
public interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM COURSE ORDER BY courseId ASC ")
    List<Course> getAllCourse();

    /*
    @Query("SELECT * FROM COURSE WHERE instructorId =:instructorId ORDER BY instructorId ASC ")
    @Ignore
    List<Course> getAllRelatedInstructor(Integer instructorId);


     */
    @Query("SELECT * FROM COURSE ORDER BY courseId ASC ")
    List<Course> getRelatedCourse();



    @Query("SELECT * FROM TERM Where courseId IN (SELECT courseId FROM COURSE WHERE courseId = :termId)")
    List<Term> getAllRelatedTerm(Integer termId);

}
