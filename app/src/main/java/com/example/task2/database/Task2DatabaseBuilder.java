package com.example.task2.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.task2.dao.CourseDAO;
import com.example.task2.dao.DateConverter;
import com.example.task2.dao.InstructorDAO;
import com.example.task2.dao.TermDAO;
import com.example.task2.entities.Course;
import com.example.task2.entities.Instructor;
import com.example.task2.entities.Term;

//When I make changes to my entities remember to change the version
@Database(entities = {Course.class, Instructor.class, Term.class},version=5,exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class Task2DatabaseBuilder extends RoomDatabase {

    public abstract CourseDAO courseDAO();

    public abstract TermDAO termDAO();

    public abstract InstructorDAO instructorDAO();

    private static volatile Task2DatabaseBuilder INSTANCE;

    static Task2DatabaseBuilder getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (Task2DatabaseBuilder.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Task2DatabaseBuilder.class,"Task2Database.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
