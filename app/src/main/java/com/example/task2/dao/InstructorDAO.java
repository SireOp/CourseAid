package com.example.task2.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.task2.entities.Instructor;

import java.util.List;

@Dao
public interface InstructorDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Instructor instructor);

    @Update
    void update(Instructor instructor);

    @Delete
    void delete(Instructor instructor);

    @Query("SELECT * FROM Instructor ORDER BY instructorId ASC ")
    List<Instructor> getAllInstructors();

    /*
    @Query("SELECT * FROM COURSE WHERE instructorId =:instructorId ORDER BY instructorId ASC ")
    List<Instructor> getAllRelatedCourse(int instructorId);


     */

}
