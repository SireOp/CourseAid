package com.example.myCourse.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.CourseWithStudents;


import java.util.List;


@Dao
public interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("DELETE FROM COURSE")
    void deleteAllCourses();

    @Query("SELECT * FROM COURSE ORDER BY courseId ASC ")
    List<Course> getAllCourse();


    @Query("SELECT * FROM COURSE WHERE termId = :termId ORDER BY courseId ASC ")
    List<Course> getRelatedCourse(int termId);

    @Query("SELECT * FROM COURSE WHERE termId = :termId")
    List<Course> getAllRelatedCourses(int termId);

    @Transaction
    @Query("SELECT * FROM course WHERE courseId = :id")
    CourseWithStudents getCourseWithStudents(int id);

}
