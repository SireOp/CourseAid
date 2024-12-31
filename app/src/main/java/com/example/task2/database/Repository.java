package com.example.task2.database;

import android.app.Application;
import android.hardware.lights.LightState;

import com.example.task2.dao.CourseDAO;
import com.example.task2.dao.InstructorDAO;
import com.example.task2.dao.TermDAO;
import com.example.task2.entities.Course;
import com.example.task2.entities.Instructor;
import com.example.task2.entities.Term;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//47:35 of vid 2
public class Repository {

    //m means it is an instance variable e if it is a static variable
    private TermDAO mTermDAO;
    private CourseDAO mCourseDAO;
    private InstructorDAO mInstructorDAO;

    private List<Term> mAllTerms;
    private List<Course> mAllCourses;
    private List<Instructor> mAllInstructors;

    private static int NUMBER_OF_THREADS=4;
    //executors open threads from the thread pool
    //This will run the database builder
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //When ever we get things from the database we get an executor
    public Repository(Application application){
        Task2DatabaseBuilder db = Task2DatabaseBuilder.getDatabase(application);
        mCourseDAO = db.courseDAO();
        mInstructorDAO = db.instructorDAO();
        mTermDAO = db.termDAO();
    }

    /*
    If we had allowed main thread queries we would use
    mAllProducts = getAlProducts and return mAllproducts without the thread in the middle
     */
    public List<Course>getRelatedCourse(){
        databaseExecutor.execute(() ->{
            mAllCourses = mCourseDAO.getRelatedCourse();
        });

        return mAllCourses;
    }

    public List<Course>getAllCourses(){
        databaseExecutor.execute(() ->{
            mAllCourses= mCourseDAO.getAllCourse();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return mAllCourses;
    }

   public void insert(Course course){
        databaseExecutor.execute(()->{
            mCourseDAO.insert(course);
        });
   }

    public void update (Course course){
        databaseExecutor.execute(()->{
            mCourseDAO.update(course);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete (Course course){
        databaseExecutor.execute(()->{
            mCourseDAO.delete(course);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Term>getAllTerms(){
        databaseExecutor.execute(() ->{
            mAllTerms= mTermDAO.getAllTerm();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return mAllTerms;
    }


    public void insert(Term term){
        databaseExecutor.execute(()->{
            mTermDAO.insert(term);
        });
    }
    public void delete (Term term){
        databaseExecutor.execute(()->{
            mTermDAO.delete(term);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update (Term term){
        databaseExecutor.execute(()->{
            mTermDAO.update(term);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public List<Instructor>getAllInstructors(){
        databaseExecutor.execute(() ->{
            mAllInstructors= mInstructorDAO.getAllInstructors();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return mAllInstructors;
    }

    public void delete (Instructor instructor){
        databaseExecutor.execute(()->{
            mInstructorDAO.delete(instructor);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update (Instructor instructor){
        databaseExecutor.execute(()->{
            mInstructorDAO.update(instructor);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
