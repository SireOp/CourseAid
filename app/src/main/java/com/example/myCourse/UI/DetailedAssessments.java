package com.example.myCourse.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCourse.R;
import com.example.myCourse.database.Repository;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedAssessments extends AppCompatActivity implements AssessmentAdapter.OnAssessmentClickListener {

    private Spinner assessmentCourseSpinner;
    private Spinner typeSpinner;
    private EditText assessmentTitle;
    private EditText assessmentStartDate;
    private EditText assessmentEndDate;
    private RecyclerView recyclerView;
    private AssessmentAdapter assessmentAdapter;

    private String startDateAssess, endDateAssess;
    private Repository repository;
    private Assessment currentAssessment;

    private int assessmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_assessment);

        repository = new Repository(getApplication());

        assessmentCourseSpinner = findViewById(R.id.assessmentCourseSpinner1);
        typeSpinner = findViewById(R.id.typeSpinner1);
        assessmentTitle = findViewById(R.id.assessmentTitleEditTxt);
        assessmentStartDate = findViewById(R.id.startDateEditTxt);
        assessmentEndDate = findViewById(R.id.endDateTxt);
        recyclerView = findViewById(R.id.AssessmentRecyView1);

        assessmentStartDate.setOnClickListener(v -> seeDatePickerDialog(true));
        assessmentEndDate.setOnClickListener(v -> seeDatePickerDialog(false));
        assessmentStartDate.setFocusable(false);
        assessmentEndDate.setFocusable(false);

        refreshRecyclerView();

        Intent intent = getIntent();
        assessmentId = intent.getIntExtra("assessmentId", -1);
        String assessmentName = intent.getStringExtra("assessmentTitle");
        String selectedType = intent.getStringExtra("type");
        String sDate = intent.getStringExtra("startDate");
        String eDate = intent.getStringExtra("endDate");
        int courseId = intent.getIntExtra("courseId", -1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (sDate != null && !sDate.isEmpty()) {
            assessmentStartDate.setText(sDate);
            try {
                startDate = LocalDate.parse(sDate, formatter);
            } catch (DateTimeParseException e) {
                Log.e("DetailedAssessments", "Invalid start date format", e);
            }
        }

        if (eDate != null && !eDate.isEmpty()) {
            assessmentEndDate.setText(eDate);
            try {
                endDate = LocalDate.parse(eDate, formatter);
            } catch (DateTimeParseException e) {
                Log.e("DetailedAssessments", "Invalid end date format", e);
            }
        }

        if (assessmentName != null) assessmentTitle.setText(assessmentName);
        if (selectedType != null && typeSpinner != null) {
            ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>) typeSpinner.getAdapter();
            int index = typeAdapter.getPosition(selectedType);
            if (index >= 0) typeSpinner.setSelection(index);
        }

        if (startDate != null && endDate != null) {
            currentAssessment = new Assessment(
                    assessmentId,
                    assessmentName != null ? assessmentName : "",
                    selectedType != null ? selectedType : "None",
                    startDate,
                    endDate,
                    courseId
            );
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        FloatingActionButton detailedTFloatBtn = findViewById(R.id.deassessmentFloatingActionBtn);
        detailedTFloatBtn.setOnClickListener(v -> {
            Intent i = new Intent(DetailedAssessments.this, Assessments.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        });
    }

    private void refreshRecyclerView() {
        new Thread(() -> {
            List<Course> allCourses = repository.getAllCourses();
            List<Assessment> allAssessments = repository.getAllAssessments();

            runOnUiThread(() -> {
                ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allCourses);
                courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                assessmentCourseSpinner.setAdapter(courseAdapter);

                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"None", "Performance", "Objective"});
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(typeAdapter);

                assessmentAdapter = new AssessmentAdapter(this, allAssessments, assessment -> {
                    currentAssessment = assessment;
                    assessmentTitle.setText(assessment.getAssessmentTitle());
                    assessmentStartDate.setText(assessment.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    assessmentEndDate.setText(assessment.getEndDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

                    for (int i = 0; i < typeSpinner.getCount(); i++) {
                        if (typeSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(assessment.getType())) {
                            typeSpinner.setSelection(i);
                            break;
                        }
                    }
                    for (int j = 0; j < assessmentCourseSpinner.getCount(); j++) {
                        Course course = (Course) assessmentCourseSpinner.getItemAtPosition(j);
                        if (course.getCourseId() == assessment.getCourseId()) {
                            assessmentCourseSpinner.setSelection(j);
                            break;
                        }
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(assessmentAdapter);
            });
        }).start();
    }

    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailedAssessments.this, (view, year1, monthOfYear, dayOfMonth) -> {
            c.set(Calendar.YEAR, year1);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            sdf.setLenient(false);
            String date = sdf.format(c.getTime());

            try {
                sdf.parse(date);
                if (isStartDate) {
                    startDateAssess = date;
                    assessmentStartDate.setText(startDateAssess);
                } else {
                    endDateAssess = date;
                    assessmentEndDate.setText(endDateAssess);
                }
            } catch (ParseException e) {
                Toast.makeText(DetailedAssessments.this, "Invalid date format. Please try again.", Toast.LENGTH_LONG).show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void scheduleAssessmentNotification(boolean isStart) {
        String dateStr = isStart ? assessmentStartDate.getText().toString() : assessmentEndDate.getText().toString();
        if (dateStr.isEmpty()) {
            Toast.makeText(this, "Date not set", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date selectedDate = sdf.parse(dateStr);
            if (selectedDate == null) return;

            long triggerTime = selectedDate.getTime();
            long currentTime = System.currentTimeMillis();

            if (!isStart) {
                String startStr = assessmentStartDate.getText().toString();
                if (!startStr.isEmpty()) {
                    Date start = sdf.parse(startStr);
                    if (start != null && selectedDate.before(start)) {
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            String message = "Assessment \"" + assessmentTitle.getText().toString().trim() +
                    (isStart ? "\" is starting today!" : "\" is ending today!");

            if (triggerTime > currentTime) {
                message = "Assessment \"" + assessmentTitle.getText().toString().trim() +
                        (isStart ? "\" is starting soon!" : "\" is ending soon!");
            }

            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("key", message);

            PendingIntent sender = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Toast.makeText(this, "Permission required to schedule exact alarms", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                try {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, sender);
                    Toast.makeText(this, "Assessment notification set", Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAssessmentClick(Assessment assessment) {
        currentAssessment = assessment;
        Toast.makeText(this, "Selected: " + assessment.getAssessmentTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_assessments, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailedAssessments.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.updateAssessment1) {
            if (currentAssessment != null) {
                String updatedTitle = assessmentTitle.getText().toString().trim();
                String updatedType = typeSpinner.getSelectedItem().toString();
                String updatedStart = assessmentStartDate.getText().toString().trim();
                String updatedEnd = assessmentEndDate.getText().toString().trim();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
                try {
                    LocalDate newStartDate = LocalDate.parse(updatedStart, formatter);
                    LocalDate newEndDate = LocalDate.parse(updatedEnd, formatter);

                    currentAssessment.setAssessmentTitle(updatedTitle);
                    currentAssessment.setType(updatedType);
                    currentAssessment.setStartDate(newStartDate);
                    currentAssessment.setEndDate(newEndDate);

                    repository.update(currentAssessment);
                    Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
                    refreshRecyclerView();
                } catch (DateTimeParseException e) {
                    Toast.makeText(this, "Invalid date format.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No assessment selected to update", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.deleteAssessment1) {
            if (currentAssessment != null) {
                repository.delete(currentAssessment);
                Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();
                refreshRecyclerView();
            } else {
                Toast.makeText(this, "No assessment selected to delete", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.notifyAssessmentStart) {
            scheduleAssessmentNotification(true);
            return true;
        }

        if (item.getItemId() == R.id.notifyAssessmentEnd) {
            scheduleAssessmentNotification(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

