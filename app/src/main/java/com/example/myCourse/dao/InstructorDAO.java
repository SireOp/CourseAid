package com.example.myCourse.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myCourse.entities.Instructor;

import java.util.List;

@Dao
public interface InstructorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Instructor instructor);

    @Update
    void update(Instructor instructor);

    @Delete
    void delete(Instructor instructor);

    @Query("DELETE FROM INSTRUCTOR")
    void deleteAllInstructors();

    @Query("SELECT * FROM Instructor ORDER BY instructorId ASC ")
    List<Instructor> getAllInstructors();


}
