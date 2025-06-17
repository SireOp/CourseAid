package com.example.myCourse.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myCourse.entities.Assessment;

import java.util.List;

@Dao
public interface AssessmentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);
    @Query("Delete FROM ASSESSMENT")
    void deleteAllAssessments();

    @Query("SELECT * FROM ASSESSMENT ORDER BY assessmentId ASC")
    List<Assessment> getAllAssessment();

    @Query("SELECT * FROM ASSESSMENT WHERE courseId = :courseId ORDER BY courseId ASC")
    List<Assessment> getAllRelatedAssessment(int courseId);

    @Query("SELECT * FROM ASSESSMENT WHERE courseId = :courseId LIMIT 1 ")
    Assessment getAssessmentByCourseId(int courseId);
}
