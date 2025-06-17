package com.example.myCourse.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Term;

import java.util.List;

@Dao
public interface TermDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Term term);

    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("Delete FROM TERM")
    void deleteAllterms();

    @Query("SELECT * FROM TERM ORDER BY termId ASC ")
    List<Term> getAllTerm();


    @Query("SELECT * FROM COURSE WHERE termId = :termId")
    List<Course> getTAllRelatedCourses(Integer termId);

    @Query("SELECT * FROM COURSE WHERE termId IN (SELECT termId FROM COURSE WHERE courseId = :courseId)")
    List<Course> getAllRelCourses(int courseId);

    @Query("SELECT * FROM term WHERE termId = :id")
    Term getTermById(Integer id);

}


