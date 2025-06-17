package com.example.myCourse.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myCourse.entities.CourseWithStudents;
import com.example.myCourse.entities.Student;
import com.example.myCourse.entities.StudentWithCourses;

import java.util.List;
@Dao
public interface StudentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("DELETE FROM STUDENT")
    void deleteAllStudents();


    @Query("SELECT * FROM STUDENT ORDER BY studentId ASC")
    List<Student> getAllStudents();

    @Transaction
    @Query("SELECT * FROM student WHERE studentId = :id")
    StudentWithCourses getStudentWithCourses(int id);





}
