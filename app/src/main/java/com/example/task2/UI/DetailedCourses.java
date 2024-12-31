package com.example.task2.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.example.task2.entities.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import com.example.task2.R;
import com.example.task2.database.Repository;
import com.example.task2.entities.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailedCourses extends AppCompatActivity {


    //1:20:49 Adding items to our courses 1st then terms from menu_detailed course
    private Button addCourseBtnDetailed, startDateBtnDCourse, endDateBtnDCourse;

    private TextView selectedSDateDTxtCourse, selectedEDateDTxtCourse;

    private String startDateDCourse, endDateDCourse;

    String cName;

    int cId;

    int termId;
    EditText courseNotesEditTxt;
    EditText courseAssessmentEditTxt;

    EditText courseInfoEditTxt;


    EditText courseNameEditTxt;

    EditText courseIdEditTxt;

    TextView courseStartDateTxt;

    TextView courseEndDateTxt;

    EditText courseInstructorName;

    Course currentCourse;

    int numCourses;

    private Repository repository;

    // Remove items to see if they are showing but hidden do to UI layout or fix the recyclerview
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_courses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.detailedCourseRecyView);
        repository = new Repository(getApplication());
        List<Course> allCourse = repository.getAllCourses();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CourseAdapter courseAdapter = new CourseAdapter(this);
        courseAdapter.setCourses(allCourse);
        recyclerView.setAdapter(courseAdapter);
        /*
        Here is where we carry of the data form the recyView to the edit txt
        Grab the extra from the intent that where sent over
         */
        FloatingActionButton termsFloatBtn = findViewById(R.id.floatingActionBtnDetailedCourse);
        courseNameEditTxt = findViewById(R.id.courseNameEditTxt);
        courseIdEditTxt = findViewById(R.id.courseIdEditTxt);
        cName = getIntent().getStringExtra("name");
        cId = getIntent().getIntExtra("id",0);
        courseNameEditTxt.setText(cName);
        courseIdEditTxt.setText(String.valueOf(cId));
        courseAssessmentEditTxt = findViewById(R.id.dCourseAssesEditTxt);
        //courseStartDateTxt = findViewById(R.id.selectedSDateTxtCourse);
        courseInfoEditTxt = findViewById(R.id.dCourseInfoEditTxt);
        courseNotesEditTxt = findViewById(R.id.dCnotesEditTxt);
        courseInstructorName = findViewById(R.id.dCourseInstructorNameEditTxt);


        termsFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailedCourses.this, Course.class);
                intent.putExtra("test1", "Info sent");
                startActivity(intent);
            }
        });

    }

    public boolean onCreateOptionMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_detailed_course, menu);
        return  true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== R.id.courseSave){
            //repository = new Repository((getApplication()));
            Course course;
            if(cId == -1){
                if(repository.getAllCourses().size() == 0) cId = 1;
                //Will get the size of the list of course index of ) so we -1 makes sure the product Id are unique
                else cId = repository.getAllCourses().get(repository.getAllCourses().size() - 1).getCourseId() + 1;
                course = new Course(cId, courseIdEditTxt.getText().toString(),courseNameEditTxt.getText().toString());
                repository.insert(course);
                Toast.makeText(DetailedCourses.this,"Course was added.", Toast.LENGTH_LONG).show();
                this.finish();
            }
            else {
                course = new Course(cId, courseIdEditTxt.getText().toString(),courseNameEditTxt.getText().toString());
                repository.update(course);
                Toast.makeText(DetailedCourses.this,"Course was updated.", Toast.LENGTH_LONG).show();
                this.finish();
            }
            if(item.getItemId() == R.id.courseDelete){
                for (Course cou:repository.getAllCourses()){
                    if (cou.getCourseId() == cId)currentCourse = cou;
                }
                numCourses = 0;
                for (Term term: repository.getAllTerms()){
                    if(term.getCourseId() == cId ) ++numCourses;
                }
            }   if (numCourses == 0){
                repository.delete(currentCourse);
                Toast.makeText(DetailedCourses.this, currentCourse.getTitle() + "was removed", Toast.LENGTH_LONG).show();
                DetailedCourses.this.finish();
            }
            else{
                Toast.makeText(DetailedCourses.this, "A Term can't be deleted until all courses are removed", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


//Fix date
        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailedCourses.this, (view, year1, monthOfYear, dayOfMonth) ->
        {
            c.set(Calendar.YEAR, year1);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

            String date = sdf.format(c.getTime());
            //year1 + "-" + (monthOfYear + 1) + "-"  + dayOfMonth;
            if (isStartDate) {
                startDateDCourse = date;
                selectedSDateDTxtCourse.setText("Term Start Date: " + startDateDCourse);
            } else {
                endDateDCourse = date;
                selectedEDateDTxtCourse.setText("Term End Date; " + endDateDCourse);
            }

            if (startDateDCourse != null && endDateDCourse != null) {
                // Place method to save to DB here
                Toast.makeText(DetailedCourses.this, "Dates have been saved", Toast.LENGTH_SHORT).show();
            }

        }, year, month, day);
        datePickerDialog.show();
    }
}