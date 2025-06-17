package com.example.myCourse.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCourse.R;
import com.example.myCourse.dao.DSM;
import com.example.myCourse.database.Repository;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Courses extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner courseStatusSpinner;
    private CourseAdapter courseAdapter;
    private Spinner courseSpinner1;
    private Repository repository;
    private DSM dsm;

    private FloatingActionButton coursesFloatBtn;

    private int courseId;
    private Integer termId;

    private EditText courseStartDate;
    private EditText courseEndDate;
    private EditText courseNameTxt;
    private EditText courseNotes;
    private EditText courseInfo;
    private EditText instructorName;
    private EditText phone;
    private EditText email;

    private String startDateCourse;
    private String endDateCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses);

        // Initialize UI references
        courseSpinner1 = findViewById(R.id.courseSpinner1);
        instructorName = findViewById(R.id.courseInstructorNameTxt);
        String instructorN = getIntent().getStringExtra("name");
        instructorName.setText(instructorN);

        courseNameTxt = findViewById(R.id.courseNameTxt);
        String courseName = getIntent().getStringExtra("title");
        courseNameTxt.setText(courseName);

        courseId = getIntent().getIntExtra("id", -1);

        courseStartDate = findViewById(R.id.courseStartDateTxt);
        String sDate = getIntent().getStringExtra("startDate");
        if (sDate != null) courseStartDate.setText(sDate);

        courseEndDate = findViewById(R.id.courseEndDateTxt);
        String eDate = getIntent().getStringExtra("endDate");
        if (eDate != null) courseEndDate.setText(eDate);

        termId = getIntent().hasExtra("termId") ? getIntent().getIntExtra("termId", -1) : null;
        if (termId != null && termId == -1) termId = null;

        courseStatusSpinner = findViewById(R.id.courseStatusSpinner);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"In Progress", "Completed", "Dropped", "Plan to Take"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseStatusSpinner.setAdapter(statusAdapter);

        String courseSt = getIntent().getStringExtra("status");
        if (courseSt != null) {
            int position = statusAdapter.getPosition(courseSt);
            if (position >= 0) courseStatusSpinner.setSelection(position);
        } else {
            courseStatusSpinner.setSelection(0);
        }

        courseNotes = findViewById(R.id.courseNotesTxt);
        courseNotes.setText(getIntent().getStringExtra("notes"));

        courseInfo = findViewById(R.id.courseInfoTxt);
        courseInfo.setText(getIntent().getStringExtra("courseInfo"));

        phone = findViewById(R.id.phoneTxt);
        phone.setText(getIntent().getStringExtra("phone"));
        email = findViewById(R.id.emailTxt);
        email.setText(getIntent().getStringExtra("email"));

        recyclerView = findViewById(R.id.CourseRecyView);
        repository = new Repository(getApplication());
        dsm = new DSM(getApplication());

        courseStartDate.setOnClickListener(v -> seeDatePickerDialog(true));
        courseEndDate.setOnClickListener(v -> seeDatePickerDialog(false));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Determine whether local Room DB is empty
        dsm.importDataFromServer(this::initializeUIFromLocal);

        coursesFloatBtn = findViewById(R.id.courseFloatingActionBtn);
        coursesFloatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Courses.this, DetailedCourses.class);
            startActivity(intent);
        });
    }

    /**
     * After Room has been re-populated, load UI components from local Repository
     */
    private void initializeUIFromLocal() {
        runOnUiThread(() -> {
            List<Term> terms = repository.getAllTerms();
            List<Course> courseList = repository.getAllCourses();
            List<Assessment> assessmentList = repository.getAllAssessments();

            if (terms == null) terms = new ArrayList<>();
            if (courseList == null) courseList = new ArrayList<>();
            if (assessmentList == null) assessmentList = new ArrayList<>();

            // Add a default “No Term Selected” at top
            List<Term> finalTerms = new ArrayList<>();
            finalTerms.add(new Term(-1, "No Term Selected", LocalDate.now(), LocalDate.now()));
            finalTerms.addAll(terms);

            // Populate term spinner
            ArrayAdapter<Term> termAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    finalTerms
            );
            termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseSpinner1.setAdapter(termAdapter);

            // Set up RecyclerView with CourseAdapter, preserving OnCourseClickListener
            CourseAdapter.OnCourseClickListener listener = (course, ignoredAssessment) -> {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                    Intent intent = new Intent(this, DetailedCourses.class);
                    intent.putExtra("id", course.getCourseId());
                    intent.putExtra("title", course.getTitle());
                    intent.putExtra("startDate", course.getStartDate().format(formatter));
                    intent.putExtra("endDate", course.getEndDate().format(formatter));
                    intent.putExtra("status", course.getStatus());
                    intent.putExtra("notes", course.getNotes());
                    intent.putExtra("courseInfo", course.getCourseInfo());
                    intent.putExtra("name", course.getInstructorName());
                    intent.putExtra("phone", course.getPhone());
                    intent.putExtra("email", course.getEmail());
                    intent.putExtra("termId", course.getTermId() != null ? course.getTermId() : -1);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load course details", Toast.LENGTH_SHORT).show();
                }
            };

            courseAdapter = new CourseAdapter(this, courseList, assessmentList, listener);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(courseAdapter);
        });
    }

    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            c.set(y, m, d);
            String formatted = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(c.getTime());

            if (isStartDate) {
                startDateCourse = formatted;
                courseStartDate.setText(formatted);
            } else {
                endDateCourse = formatted;
                courseEndDate.setText(formatted);
            }

            if (startDateCourse != null && endDateCourse != null) {
                Toast.makeText(this, "Dates saved", Toast.LENGTH_SHORT).show();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private boolean isValidDate(String start, String end) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
        try {
            LocalDate s = LocalDate.parse(start, df);
            LocalDate e = LocalDate.parse(end, df);
            return !s.isAfter(e);
        } catch (Exception e) {
            return false;
        }
    }

    private void validateAndSave() {
        String courseName = courseNameTxt.getText().toString().trim();
        String startDateInput = courseStartDate.getText().toString().trim();
        String endDateInput = courseEndDate.getText().toString().trim();
        String courseS = courseStatusSpinner.getSelectedItem().toString().trim();
        String courseNot = courseNotes.getText().toString().trim();
        String courseIn = courseInfo.getText().toString().trim();
        String coursePhone = phone.getText().toString().trim();
        String courseEmail = email.getText().toString().trim();
        String instructorNa = instructorName.getText().toString().trim();

        Term selectedTerm = (Term) courseSpinner1.getSelectedItem();
        Integer selectedTermId = (selectedTerm != null && selectedTerm.getTermId() != -1)
                ? selectedTerm.getTermId() : null;

        if (courseName.isEmpty() || startDateInput.isEmpty() || endDateInput.isEmpty()) {
            Toast.makeText(this, "Enter valid course info", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidDate(startDateInput, endDateInput)) {
            Toast.makeText(this, "Invalid date range", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            LocalDate start = LocalDate.parse(startDateInput, DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
            LocalDate end = LocalDate.parse(endDateInput, DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            if (courseId == -1) {
                int newId = repository.getAllCourses().isEmpty()
                        ? 1
                        : repository.getAllCourses()
                        .get(repository.getAllCourses().size() - 1)
                        .getCourseId() + 1;

                Course newCourse = new Course(
                        newId,
                        courseName,
                        start,
                        end,
                        courseS,
                        courseNot,
                        courseIn,
                        instructorNa,
                        coursePhone,
                        courseEmail,
                        selectedTermId
                );
                repository.insert(newCourse);
                dsm.pushCourse(newCourse, () ->
                        dsm.importDataFromServer(this::initializeUIFromLocal)
                );

                Toast.makeText(this, "Course saved", Toast.LENGTH_SHORT).show();

            } else {
                Course updated = new Course(
                        courseId,
                        courseName,
                        start,
                        end,
                        courseS,
                        courseNot,
                        courseIn,
                        instructorNa,
                        coursePhone,
                        courseEmail,
                        selectedTermId
                );
                repository.update(updated);
                // after repository.insert(newCourse):
                dsm.pushCourse(updated, () ->
                        dsm.importDataFromServer(this::initializeUIFromLocal)
                );

                Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
            }

            refreshRecyclerView();
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Date parsing failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshRecyclerView() {
        List<Course> updatedCourses = repository.getAllCourses();
        List<Assessment> updatedAssessments = repository.getAllAssessments();

        courseAdapter = new CourseAdapter(
                this,
                updatedCourses != null ? updatedCourses : new ArrayList<>(),
                updatedAssessments != null ? updatedAssessments : new ArrayList<>(),
                (course, ignoredAssessment) -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                        Intent intent = new Intent(Courses.this, DetailedCourses.class);
                        intent.putExtra("id", course.getCourseId());
                        intent.putExtra("title", course.getTitle());
                        intent.putExtra("startDate", course.getStartDate().format(formatter));
                        intent.putExtra("endDate", course.getEndDate().format(formatter));
                        intent.putExtra("status", course.getStatus());
                        intent.putExtra("notes", course.getNotes());
                        intent.putExtra("courseInfo", course.getCourseInfo());
                        intent.putExtra("name", course.getInstructorName());
                        intent.putExtra("phone", course.getPhone());
                        intent.putExtra("email", course.getEmail());
                        intent.putExtra("termId", course.getTermId() != null ? course.getTermId() : -1);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load course details", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        recyclerView.setAdapter(courseAdapter);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Courses.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.addCourse || item.getItemId() == R.id.updateCourse) {
            int newId = repository.getAllCourses().isEmpty() ? 1 :
                    repository.getAllCourses().get(repository.getAllCourses().size() - 1).getCourseId() + 1;


            validateAndSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // After returning from another screen, refresh so local changes appear
        refreshRecyclerView();
    }
}
