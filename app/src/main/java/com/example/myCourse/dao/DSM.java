// src/main/java/com/example/myCourse/dao/DSM.java

package com.example.myCourse.dao;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.example.myCourse.database.Repository;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Instructor;
import com.example.myCourse.entities.Student;
import com.example.myCourse.entities.StudentCourseCrossRef;
import com.example.myCourse.entities.StudentWithCourses;
import com.example.myCourse.entities.Term;
import com.example.myCourse.network.ApiClient;
import com.example.myCourse.network.ApiService;
import com.example.myCourse.utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DSM is responsible for syncing data between Room and the server.
 */
public class DSM {
    private static final String TAG = "DSM";

    private final Repository repo;
    private final Gson gson;
    private final ApiService apiService;

    public DSM(Application app) {
        this.repo = new Repository(app);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        this.apiService = ApiClient.getClient().create(ApiService.class);
        Log.d(TAG, "Initialized DSM with BASE_URL = " + ApiClient.getClient().baseUrl());
    }

    /**
     * Pushes only new local records to the server, one entity at a time.
     * This avoids wiping out the server’s JSON on every sync.
     */
    public void syncAllData(Runnable onFinished) {
        Log.d(TAG, "→ Starting syncAllData()");

        // 1) Sync Terms
        List<Term> terms = repo.getAllTerms();
        Log.d(TAG, "Found " + (terms != null ? terms.size() : 0) + " local terms to push");
        if (terms != null) {
            for (Term t : terms) {
                JsonObject termJson = gson.toJsonTree(t).getAsJsonObject();
                apiService.createTerm(termJson).enqueue(new Callback<JsonObject>() {
                    @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "❌ Failed to push Term (ID=" + t.getTermId() + "): HTTP " + response.code());
                        } else {
                            Log.d(TAG, "✅ Pushed Term (ID=" + t.getTermId() + ")");
                        }
                    }
                    @Override public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Log.e(TAG, "❌ Error pushing Term (ID=" + t.getTermId() + "): " + throwable.getMessage());
                    }
                });
            }
        }

        // 2) Sync Courses
        List<Course> courses = repo.getAllCourses();
        Log.d(TAG, "Found " + (courses != null ? courses.size() : 0) + " local courses to push");
        if (courses != null) {
            for (Course c : courses) {
                JsonObject courseJson = gson.toJsonTree(c).getAsJsonObject();
                apiService.createCourse(courseJson).enqueue(new Callback<JsonObject>() {
                    @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "❌ Failed to push Course (ID=" + c.getCourseId() + "): HTTP " + response.code());
                        } else {
                            Log.d(TAG, "✅ Pushed Course (ID=" + c.getCourseId() + ")");
                        }
                    }
                    @Override public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Log.e(TAG, "❌ Error pushing Course (ID=" + c.getCourseId() + "): " + throwable.getMessage());
                    }
                });
            }
        }

        // 3) Sync Assessments
        List<Assessment> assessments = repo.getAllAssessments();
        Log.d(TAG, "Found " + (assessments != null ? assessments.size() : 0) + " local assessments to push");
        if (assessments != null) {
            for (Assessment a : assessments) {
                JsonObject assessJson = gson.toJsonTree(a).getAsJsonObject();
                apiService.createAssessment(assessJson).enqueue(new Callback<JsonObject>() {
                    @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "❌ Failed to push Assessment (ID=" + a.getAssessmentId() + "): HTTP " + response.code());
                        } else {
                            Log.d(TAG, "✅ Pushed Assessment (ID=" + a.getAssessmentId() + ")");
                        }
                    }
                    @Override public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Log.e(TAG, "❌ Error pushing Assessment (ID=" + a.getAssessmentId() + "): " + throwable.getMessage());
                    }
                });
            }
        }

        // 4) Sync Students
        List<Student> students = repo.getAllStudents();
        Log.d(TAG, "Found " + (students != null ? students.size() : 0) + " local students to push");
        if (students != null) {
            for (Student s : students) {
                JsonObject studentJson = gson.toJsonTree(s).getAsJsonObject();
                apiService.createStudent(studentJson).enqueue(new Callback<JsonObject>() {
                    @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "❌ Failed to push Student (ID=" + s.getStudentId() + "): HTTP " + response.code());
                        } else {
                            Log.d(TAG, "✅ Pushed Student (ID=" + s.getStudentId() + ")");
                        }
                    }
                    @Override public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Log.e(TAG, "❌ Error pushing Student (ID=" + s.getStudentId() + "): " + throwable.getMessage());
                    }
                });
            }
        }

        Log.d(TAG, "✅ syncAllData dispatched. New local records are being pushed.");
        if (onFinished != null) onFinished.run();
    }

    /** Push only Terms to server. */
    /**
     * Push exactly one Term to the server.
     * @param t          the Term to push
     * @param onFinished a callback to run once the network request has been enqueued
     */
    public void pushTerm(Term t, Runnable onFinished) {
        Log.d(TAG, "→ Pushing single Term (ID=" + t.getTermId() + ")");

        // Serialize the Term into a JsonObject
        JsonObject json = gson.toJsonTree(t).getAsJsonObject();

        // Enqueue exactly one POST /admin/terms
        apiService.createTerm(json).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG,
                            "❌ Failed to push Term (ID=" + t.getTermId()
                                    + "): HTTP " + response.code()
                    );
                } else {
                    Log.d(TAG,
                            "✅ Successfully pushed Term (ID=" + t.getTermId() + ")"
                    );
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Log.e(TAG,
                        "❌ Error pushing Term (ID=" + t.getTermId()
                                + "): " + throwable.getMessage()
                );
            }
        });

        // Signal completion immediately after enqueue (network still in flight)
        if (onFinished != null) {
            Log.d(TAG, "✅ pushTerm dispatched for Term (ID=" + t.getTermId() + ")");
            onFinished.run();
        }
    }


    /**
     * Push exactly one Course to the server.
     *
     * @param c          the Course to push
     * @param onFinished callback to invoke once the network request is enqueued
     */
    public void pushCourse(Course c, Runnable onFinished) {
        Log.d(TAG, "→ Pushing single Course (ID=" + c.getCourseId() + ")");

        // Serialize the Course into JSON
        JsonObject json = gson.toJsonTree(c).getAsJsonObject();

        // Enqueue exactly one POST /admin/courses
        apiService.createCourse(json).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG,
                            "❌ Failed to push Course (ID=" + c.getCourseId()
                                    + "): HTTP " + response.code()
                    );
                } else {
                    Log.d(TAG,
                            "✅ Successfully pushed Course (ID=" + c.getCourseId() + ")"
                    );
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG,
                        "❌ Error pushing Course (ID=" + c.getCourseId()
                                + "): " + t.getMessage()
                );
            }
        });

        // Signal completion immediately after enqueue (network still in flight)
        if (onFinished != null) {
            Log.d(TAG,
                    "✅ pushCourse dispatched for Course (ID=" + c.getCourseId() + ")"
            );
            onFinished.run();
        }
    }

    // In DSM.java, replace syncAssessments(...) with:

    /**
     * Push exactly one Assessment to the server.
     *
     * @param a          the Assessment to push
     * @param onFinished a callback to invoke once the network request is enqueued
     */
    public void pushAssessment(Assessment a, Runnable onFinished) {
        Log.d(TAG, "→ Pushing single Assessment (ID=" + a.getAssessmentId() + ")");

        // Serialize the Assessment into JSON
        JsonObject json = gson.toJsonTree(a).getAsJsonObject();

        // Enqueue exactly one POST /admin/assessments
        apiService.createAssessment(json).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG,
                            "❌ Failed to push Assessment (ID=" + a.getAssessmentId()
                                    + "): HTTP " + response.code()
                    );
                } else {
                    Log.d(TAG,
                            "✅ Successfully pushed Assessment (ID=" + a.getAssessmentId() + ")"
                    );
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG,
                        "❌ Error pushing Assessment (ID=" + a.getAssessmentId()
                                + "): " + t.getMessage()
                );
            }
        });

        // Signal completion immediately after enqueue (network still in flight)
        if (onFinished != null) {
            Log.d(TAG,
                    "✅ pushAssessment dispatched for Assessment (ID=" + a.getAssessmentId() + ")"
            );
            onFinished.run();
        }
    }


    /** Push only Students to server. */
    public void syncStudents(Runnable onFinished) {
        Log.d(TAG, "→ Starting syncStudents()");
        List<Student> students = repo.getAllStudents();
        Log.d(TAG, "Found " + (students != null ? students.size() : 0) + " local students to push");
        if (students != null) {
            for (Student s : students) {
                JsonObject json = gson.toJsonTree(s).getAsJsonObject();
                apiService.createStudent(json).enqueue(new Callback<JsonObject>() {
                    @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "❌ Failed to push Student (ID=" + s.getStudentId() + "): HTTP " + response.code());
                        } else {
                            Log.d(TAG, "✅ Pushed Student (ID=" + s.getStudentId() + ")");
                        }
                    }
                    @Override public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Log.e(TAG, "❌ Error pushing Student (ID=" + s.getStudentId() + "): " + throwable.getMessage());
                    }
                });
            }
        }
        Log.d(TAG, "✅ syncStudents dispatched.");
        if (onFinished != null) onFinished.run();
    }

    /**
     * Completely clears local Room tables and re‐populates them with server data.
     * All clears + inserts run in one ordered background task, and onFinished()
     * only fires once every insert (and FK‐skip) has completed.
     */
    public void importDataFromServer(Runnable onFinished) {
        Log.d(TAG, "→ Calling GET /data on server");
        apiService.getFullSync().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "❌ importDataFromServer: Empty or error response (HTTP " + response.code() + ")");
                    if (onFinished != null) onFinished.run();
                    return;
                }

                JsonObject data = response.body();
                // Parse JSON arrays on this thread
                // DEBUG: what did the server actually send for "terms"?
                Log.d("DSM", "Raw server terms JSON: " + data.get("terms").toString());

                Type termListType       = new TypeToken<List<Term>>() {}.getType();

                Type courseListType     = new TypeToken<List<Course>>() {}.getType();
                Type instructorListType = new TypeToken<List<Instructor>>() {}.getType();
                Type assessmentListType = new TypeToken<List<Assessment>>() {}.getType();
                Type swcListType        = new TypeToken<List<StudentWithCourses>>() {}.getType();

                final List<Term> terms             = gson.fromJson(data.get("terms"), termListType);
                final List<Course> courses         = gson.fromJson(data.get("courses"), courseListType);
                final List<Instructor> instructors = gson.fromJson(data.get("instructors"), instructorListType);
                final List<Assessment> assessments = gson.fromJson(data.get("assessments"), assessmentListType);
                final List<StudentWithCourses> swc  = gson.fromJson(data.get("studentsWithCourses"), swcListType);

                Log.d(TAG, "→ Received: " +
                        (terms != null ? terms.size() : 0) + " terms, " +
                        (courses != null ? courses.size() : 0) + " courses, " +
                        (instructors != null ? instructors.size() : 0) + " instructors, " +
                        (assessments != null ? assessments.size() : 0) + " assessments, " +
                        (swc != null ? swc.size() : 0) + " studentsWithCourses."
                );

                // *** Run all DB work in one ordered executor task ***
                Repository.databaseExecutor.execute(() -> {
                    try {
                        Log.d(TAG, "→ Clearing local Room tables");
                        repo.deleteAllAssessments();
                        repo.deleteAllCourses();
                        repo.deleteAllTerms();
                        repo.deleteAllInstructors();
                        repo.deleteAllStudents();

                        // 1) Terms
                        if (terms != null) {
                            for (Term t : terms) {
                                try {
                                    repo.insertTermSync(t);
                                    Log.d(TAG, "Inserted Term (ID=" + t.getTermId() + ")");
                                } catch (Exception e) {
                                    Log.w(TAG, "⚠️ Skipping Term (ID=" + t.getTermId()
                                            + "): " + e.getMessage());
                                }
                            }
                        }

                        if (courses != null) {
                            for (Course c : courses) {
                                try {
                                    repo.insertCourseSync(c);
                                    Log.d(TAG, "Inserted Course (ID=" + c.getCourseId() + ")");
                                } catch (Exception e) {
                                    Log.w(TAG, "⚠️ Skipping Course (ID=" + c.getCourseId()
                                            + "): " + e.getMessage());
                                }
                            }
                        }

                        // 3) Instructors
                        if (instructors != null) {
                            for (Instructor i : instructors) {
                                try {
                                    repo.insert(i);
                                    Log.d(TAG, "Inserted Instructor (ID=" + i.getInstructorId() + ")");
                                } catch (Exception e) {
                                    Log.w(TAG, "⚠️ Skipping Instructor (ID=" + i.getInstructorId()
                                            + "): " + e.getMessage());
                                }
                            }
                        }

                        // 4) Assessments (skip orphan FKs)
                        if (assessments != null) {
                            for (Assessment a : assessments) {
                                try {
                                    repo.insertAssessmentSync(a);
                                    Log.d(TAG, "Inserted Assessment (ID=" + a.getAssessmentId() + ")");
                                } catch (SQLiteConstraintException e) {
                                    Log.w(TAG, "⚠️ Skipping orphan Assessment (ID="
                                            + a.getAssessmentId() + "): " + e.getMessage());
                                }
                            }
                        }

                        // 5) Students + CrossRefs
                        if (swc != null) {
                            for (StudentWithCourses entry : swc) {
                                Student s = entry.student;
                                try {
                                    repo.insertStudentSync(s);
                                    Log.d(TAG, "Inserted Student (ID=" + s.getStudentId() + ")");
                                } catch (Exception e) {
                                    Log.w(TAG, "⚠️ Skipping Student (ID=" + s.getStudentId()
                                            + "): " + e.getMessage());
                                }


                                for (Course linked : entry.courses) {
                                    // defensive insert for course
                                    try { repo.insertCourseSync(linked); }
                                    catch (Exception ignore) { }

                                    // cross‐ref with FK safety
                                    try {
                                        repo.insertCrossRefSync(
                                                new StudentCourseCrossRef(
                                                        s.getStudentId(),
                                                        linked.getCourseId()
                                                )
                                        );
                                        Log.d(TAG,
                                                "    ⇢ CrossRef Student "
                                                        + s.getStudentId()
                                                        + " ↔ Course "
                                                        + linked.getCourseId()
                                        );
                                    } catch (SQLiteConstraintException e) {
                                        Log.w(TAG,
                                                "⚠️ Skipping orphan CrossRef Student "
                                                        + s.getStudentId()
                                                        + " ↔ Course "
                                                        + linked.getCourseId()
                                                        + ": " + e.getMessage()
                                        );
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "✅ importDataFromServer: Local DB fully updated.");
                    } catch (Exception e) {
                        Log.e(TAG, "❌ DB import error: " + e.getMessage());
                    }

                    // **Finally**, notify the caller/UI
                    if (onFinished != null) {
                        // Post back to the main thread:
                        new android.os.Handler(android.os.Looper.getMainLooper())
                                .post(onFinished);
                    }
                });

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "❌ importDataFromServer failed: " + t.getMessage());
                if (onFinished != null) onFinished.run();
            }
        });
    }
}
