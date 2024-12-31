package com.example.task2.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.task2.entities.Course;
import com.example.task2.entities.Instructor;
import com.example.task2.entities.Term;

import java.util.List;

@Dao
public interface TermDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Term term);

    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("SELECT * FROM TERM ORDER BY termId ASC ")
    List<Term> getAllTerm();


    @Query("SELECT * FROM COURSE WHERE termId = :termId")
    List<Course>   getAllRelatedCourses(int termId);

    @Query("SELECT * FROM COURSE WHERE termId IN (SELECT termId FROM COURSE WHERE courseId = :courseId)")
    List<Course> getAllRelCourses(int courseId);
}


