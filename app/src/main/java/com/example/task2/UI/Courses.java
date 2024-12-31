package com.example.task2.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task2.R;
import com.example.task2.database.Repository;
import com.example.task2.entities.Course;
import com.example.task2.entities.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Courses extends AppCompatActivity {

    private Repository repository;

    ArrayList<Courses> courseList;


    ArrayAdapter<Course> courseArrayAdapter;


    private TextView selectedSDatetxtCourse, selectedEDatetxtCourse;

    private EditText courseNameTxt;

    private Button startDateBtnCourse,courseAddBtn,endDateBtnCourse;

    private String startDateCourse, endDateCourse;

    private int year, month, day;
/*
Timestamp 50:34 get Recycle view working
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses);

        selectedSDatetxtCourse  = findViewById(R.id.selectedSDateTxtCourse);
        selectedEDatetxtCourse = findViewById(R.id.selectedEDateTxtCourse);
        courseAddBtn = findViewById(R.id.courseAddBtn);
        startDateBtnCourse = findViewById(R.id.startDateBtnCourse);
        endDateBtnCourse= findViewById(R.id.endDateBtnCourse);


        startDateBtnCourse.setOnClickListener(v -> seeDatePickerDialog(true));
        endDateBtnCourse.setOnClickListener(v -> seeDatePickerDialog(false));
        System.out.println(getIntent().getStringExtra("test"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.CourseRecyView);
        repository = new Repository(getApplication());
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Course> allCourses = repository.getAllCourses();
        courseAdapter.setCourses(allCourses);



        courseList = new ArrayList<>();

        FloatingActionButton coursefloatingActionButton = findViewById(R.id.courseFloatingActionBtn);
        coursefloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new  Intent(Courses.this, DetailedCourses.class);
                intent.putExtra("test1", "Info sent");
                startActivity(intent);


            }
        });

/*
        courseList = new ArrayList<>();

        courseAddBtn.setOnClickListener(new View.OnClickListener() {



        });


 */


    }

    private  void seeDatePickerDialog(boolean isStartDate){
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Courses.this,(view,year1,monthOfYear, dayOfMonth) ->
        {
            c.set(Calendar.YEAR, year1);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String date = sdf.format(c.getTime());
            //year1 + "-" + (monthOfYear + 1) + "-"  + dayOfMonth;
            if(isStartDate){
                startDateCourse = date;
                selectedSDatetxtCourse.setText("Course Start Date: " + startDateCourse);
            } else{
                endDateCourse = date;
                selectedEDatetxtCourse.setText("Course End Date: " + endDateCourse);
            }

            if(startDateCourse !=null && endDateCourse !=null ){
                // Place method to save to DB here
                Toast.makeText(Courses.this, "Dates have been saved", Toast.LENGTH_SHORT).show();
            }

        }, year, month, day);
       datePickerDialog.show();
    }

    //Save dates to DB

}