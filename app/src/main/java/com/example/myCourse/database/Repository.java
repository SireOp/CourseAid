package com.example.myCourse.database;

import android.app.Application;

import com.example.myCourse.dao.AssessmentDAO;
import com.example.myCourse.dao.CourseDAO;
import com.example.myCourse.dao.InstructorDAO;
import com.example.myCourse.dao.StudentDAO;
import com.example.myCourse.dao.StudentCourseCrossRefDAO;
import com.example.myCourse.dao.TermDAO;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.CourseWithStudents;
import com.example.myCourse.entities.Instructor;
import com.example.myCourse.entities.Student;
import com.example.myCourse.entities.StudentCourseCrossRef;
import com.example.myCourse.entities.StudentWithCourses;
import com.example.myCourse.entities.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
/**
 * Central repository for app data operations.
 */
public class Repository {

    private TermDAO mTermDAO;
    private CourseDAO mCourseDAO;
    private InstructorDAO mInstructorDAO;
    private AssessmentDAO mAssessmentDAO;
    private StudentDAO mStudentDAO;
    private StudentCourseCrossRefDAO mStudentCourseCrossRefDAO;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        Task2DatabaseBuilder db = Task2DatabaseBuilder.getDatabase(application);
        mCourseDAO = db.courseDAO();
        mInstructorDAO = db.instructorDAO();
        mTermDAO = db.termDAO();
        mAssessmentDAO = db.assessmentDAO();
        mStudentDAO = db.studentDAO();
        mStudentCourseCrossRefDAO = db.studentCourseCrossRefDAO();
    }

    // --- Instructor operations ---
    public List<Instructor> getAllInstructors() {
        try {
            Future<List<Instructor>> future = databaseExecutor.submit(() -> mInstructorDAO.getAllInstructors());
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void insert(Instructor instructor) {
        databaseExecutor.execute(() -> mInstructorDAO.insert(instructor));
    }

    public void update(Instructor instructor) {
        databaseExecutor.execute(() -> mInstructorDAO.update(instructor));
    }

    public void delete(Instructor instructor) {
        databaseExecutor.execute(() -> mInstructorDAO.delete(instructor));
    }

    public void deleteAllInstructors() {
        databaseExecutor.execute(() -> mInstructorDAO.deleteAllInstructors());
    }

    // --- Term operations ---
    public List<Term> getAllTerms() {
        try {
            Future<List<Term>> future = databaseExecutor.submit(() -> mTermDAO.getAllTerm());
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void insert(Term term) {
        databaseExecutor.execute(() -> mTermDAO.insert(term));
    }
    public void update(Term term) {
        databaseExecutor.execute(() -> mTermDAO.update(term));
    }
    public void delete(Term term) {
        databaseExecutor.execute(() -> mTermDAO.delete(term));
    }
    public void deleteAllTerms() {
        databaseExecutor.execute(() -> mTermDAO.deleteAllterms());
    }

    // --- Course operations ---
    public List<Course> getAllCourses() {
        try {
            Future<List<Course>> future = databaseExecutor.submit(() -> mCourseDAO.getAllCourse());
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Course> getCoursesByTermId(Integer termId) {
        try {
            Future<List<Course>> future = databaseExecutor.submit(() -> mCourseDAO.getAllRelatedCourses(termId));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void insert(Course course) {
        databaseExecutor.execute(() -> {
            try {
                Integer termId = course.getTermId();
                // your existing guard: skip if parent Term is missing
                if (termId != null && mTermDAO.getTermById(termId) == null) return;
                mCourseDAO.insert(course);
            } catch (SQLiteConstraintException e) {
                Log.w("Repository",
                        "⚠️ Skipping Course insert (FK missing): ID="
                                + course.getCourseId()
                                + " → " + e.getMessage());
            }
        });
    }
    public void update(Course course) {
        databaseExecutor.execute(() -> mCourseDAO.update(course));
    }
    public void delete(Course course) {
        databaseExecutor.execute(() -> mCourseDAO.delete(course));
    }
    public void deleteAllCourses() {
        databaseExecutor.execute(() -> mCourseDAO.deleteAllCourses());
    }
    public void getCourseWithStudents(int courseId, Consumer<CourseWithStudents> cb) {
        databaseExecutor.execute(() -> {
            CourseWithStudents cs = mCourseDAO.getCourseWithStudents(courseId);
            cb.accept(cs);
        });
    }

    // --- Assessment operations ---
    public List<Assessment> getAllAssessments() {
        try {
            Future<List<Assessment>> future = databaseExecutor.submit(() -> mAssessmentDAO.getAllAssessment());
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public Assessment getAssessmentByCourseId(int courseId) {
        try {
            Future<Assessment> future = databaseExecutor.submit(() -> mAssessmentDAO.getAssessmentByCourseId(courseId));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Assessment> getAllRelatedAssessment(int courseId) {
        try {
            Future<List<Assessment>> future = databaseExecutor.submit(() -> mAssessmentDAO.getAllRelatedAssessment(courseId));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void insert(Assessment assessment) {
        databaseExecutor.execute(() -> {
            try {
                mAssessmentDAO.insert(assessment);
            } catch (SQLiteConstraintException e) {
                Log.w("Repository",
                        "⚠️ Skipping Assessment insert (FK missing): ID="
                                + assessment.getAssessmentId()
                                + " → " + e.getMessage());
            }
        });
    }
    public void update(Assessment assessment) {
        databaseExecutor.execute(() -> mAssessmentDAO.update(assessment));
    }
    public void delete(Assessment assessment) {
        databaseExecutor.execute(() -> mAssessmentDAO.delete(assessment));
    }
    public void deleteAllAssessments() {
        databaseExecutor.execute(() -> mAssessmentDAO.deleteAllAssessments());
    }

    // --- Student operations ---
    public List<Student> getAllStudents() {
        try {
            Future<List<Student>> future = databaseExecutor.submit(() -> mStudentDAO.getAllStudents());
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void insert(Student student) {
        databaseExecutor.execute(() -> {
            try {
                mStudentDAO.insert(student);
            } catch (SQLiteConstraintException e) {
                Log.w("Repository",
                        "⚠️ Skipping Student insert (FK missing): ID="
                                + student.getStudentId()
                                + " → " + e.getMessage());
            }
        });
    }
    public void update(Student student) {
        databaseExecutor.execute(() -> mStudentDAO.update(student));
    }
    public void delete(Student student) {
        databaseExecutor.execute(() -> mStudentDAO.delete(student));
    }
    public void deleteAllStudents() {
        databaseExecutor.execute(() -> mStudentDAO.deleteAllStudents());
    }
    public void getStudentWithCourses(int studentId, Consumer<StudentWithCourses> cb) {
        databaseExecutor.execute(() -> {
            StudentWithCourses sc = mStudentDAO.getStudentWithCourses(studentId);
            cb.accept(sc);
        });
    }

    /**
     * Return every StudentCourseCrossRef row (if needed).
     */
    public List<StudentCourseCrossRef> getAllStudentCourseCrossRefs() {
        try {
            Future<List<StudentCourseCrossRef>> future =
                    databaseExecutor.submit(() -> mStudentCourseCrossRefDAO.getAll());
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Return every student with their enrolled courses.
     */
    public List<StudentWithCourses> getAllStudentsWithCourses() {
        try {
            Future<List<StudentWithCourses>> future =
                    databaseExecutor.submit(() -> {
                        List<StudentWithCourses> list = new ArrayList<>();
                        List<Student> students = mStudentDAO.getAllStudents();
                        for (Student s : students) {
                            list.add(mStudentDAO.getStudentWithCourses(s.getStudentId()));
                        }
                        return list;
                    });
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Insert a StudentCourseCrossRef to record enrollment.
     */
    public void insert(StudentCourseCrossRef crossRef) {
        databaseExecutor.execute(() -> {
            try {
                mStudentCourseCrossRefDAO.insert(crossRef);
            } catch (SQLiteConstraintException e) {
                Log.w("Repository", "Skipping CrossRef " + crossRef + ": " + e.getMessage());
            }
        });
    }

    // After all your existing methods in Repository.java…

    /** Inserts a Term right now on this thread (no new Runnable). */
    public void insertTermSync(Term term) {
        mTermDAO.insert(term);
    }

    /** Inserts a Course right now on this thread. */
    public void insertCourseSync(Course course) {
        // you can keep your FK‐guard here if you like:
        Integer parentTerm = course.getTermId();
        if (parentTerm == null || mTermDAO.getTermById(parentTerm) != null) {
            mCourseDAO.insert(course);
        }
    }

    /** Inserts an Assessment right now on this thread. */
    public void insertAssessmentSync(Assessment assessment) {
        mAssessmentDAO.insert(assessment);
    }

    /** Inserts a Student right now on this thread. */
    public void insertStudentSync(Student student) {
        mStudentDAO.insert(student);
    }

    /** Inserts a cross-ref right now on this thread. */
    public void insertCrossRefSync(StudentCourseCrossRef x) {
        mStudentCourseCrossRefDAO.insert(x);
    }

}
