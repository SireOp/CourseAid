package com.example.myCourse.network;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// Instead of posting one huge “/sync” payload, we now define one endpoint per entity.
// The client will use these to push new/updated rows individually.

public interface ApiService {

    // Fetch entire server data:
    @GET("/sync")
    Call<JsonObject> getFullSync();
    // Create a new term on the server:
    @POST("/admin/terms")
    Call<JsonObject> createTerm(@Body JsonObject termPayload);

    // Create a new course on the server:
    @POST("/admin/courses")
    Call<JsonObject> createCourse(@Body JsonObject coursePayload);

    // Create a new assessment on the server:
    @POST("/admin/assessments")
    Call<JsonObject> createAssessment(@Body JsonObject assessmentPayload);

    // Create a new student on the server:
    @POST("/admin/students")
    Call<JsonObject> createStudent(@Body JsonObject studentPayload);

    // …you can add update/delete endpoints here as needed (e.g. PUT or DELETE).
}
