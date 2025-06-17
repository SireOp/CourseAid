package com.example.myCourse.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myCourse.dao.AssessmentDAO;
import com.example.myCourse.dao.CourseDAO;
import com.example.myCourse.dao.InstructorDAO;
import com.example.myCourse.dao.StudentCourseCrossRefDAO;
import com.example.myCourse.dao.StudentDAO;
import com.example.myCourse.dao.TermDAO;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Instructor;
import com.example.myCourse.entities.Student;
import com.example.myCourse.entities.StudentCourseCrossRef;
import com.example.myCourse.entities.Term;
import com.example.myCourse.utils.LDConverter;

@Database(entities = {
        Course.class,
        Instructor.class,
        Term.class,
        Assessment.class,
        Student.class,
        StudentCourseCrossRef.class
}, version = 30, exportSchema = false)
@TypeConverters({LDConverter.class})
public abstract class Task2DatabaseBuilder extends RoomDatabase {

    public abstract CourseDAO courseDAO();

    public abstract TermDAO termDAO();

    public abstract InstructorDAO instructorDAO();

    public abstract AssessmentDAO assessmentDAO();

    public abstract StudentDAO studentDAO();

    public abstract StudentCourseCrossRefDAO studentCourseCrossRefDAO();

    private static volatile Task2DatabaseBuilder INSTANCE;

    static Task2DatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Task2DatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Task2DatabaseBuilder.class, "Task2Database.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
