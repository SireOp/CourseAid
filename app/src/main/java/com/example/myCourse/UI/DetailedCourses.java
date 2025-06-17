package com.example.myCourse.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.myCourse.entities.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedCourses extends AppCompatActivity {

    private EditText courseIdEditTxt, courseNameEditTxt, startDateEditTxt, endDateEditTxt,
            dCoursePhoneEditTxt, dCnotesEditTxt, dCourseInfoEditTxt,
            dCourseInstructorNameEditTxt, dCourseEmailEditTxt;

    private Spinner courseSpinner, courseStatusSpinner1;
    private RecyclerView detailedCourseRecyView;
    private Repository repository;
    private Course currentCourse;
    private CourseAdapter courseAdapter;
    private String startDateStr, endDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_courses);

        repository = new Repository(getApplication());

        courseIdEditTxt = findViewById(R.id.courseIdEditTxt);
        courseNameEditTxt = findViewById(R.id.courseNameEditTxt);
        startDateEditTxt = findViewById(R.id.startDateEditTxt);
        endDateEditTxt = findViewById(R.id.endDateEditTxt);
        dCoursePhoneEditTxt = findViewById(R.id.dCoursePhoneEditTxt);
        dCnotesEditTxt = findViewById(R.id.dCnotesEditTxt);
        dCourseInfoEditTxt = findViewById(R.id.dCourseInfoEditTxt);
        dCourseInstructorNameEditTxt = findViewById(R.id.dCourseInstructorNameEditTxt);
        dCourseEmailEditTxt = findViewById(R.id.dCourseEmailEditTxt);
        courseSpinner = findViewById(R.id.courseSpinner);
        courseStatusSpinner1 = findViewById(R.id.courseStatusSpinner1);
        detailedCourseRecyView = findViewById(R.id.detailedCourseRecyView);
        detailedCourseRecyView.setLayoutManager(new LinearLayoutManager(this));

        startDateEditTxt.setFocusable(false);
        endDateEditTxt.setFocusable(false);
        startDateEditTxt.setOnClickListener(v -> showDatePicker(true));
        endDateEditTxt.setOnClickListener(v -> showDatePicker(false));

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"In Progress", "Completed", "Dropped", "Plan to Take"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseStatusSpinner1.setAdapter(statusAdapter);

        refreshRecyclerView();

        FloatingActionButton detailedTFloatBtn = findViewById(R.id.floatingActionBtnDetailedCourse);
        detailedTFloatBtn.setOnClickListener(view -> {
            Intent i = new Intent(DetailedCourses.this, Course.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });
    }

    private void refreshRecyclerView() {
        new Thread(() -> {
            List<Term> terms = repository.getAllTerms();
            List<Course> courses = repository.getAllCourses();
            List<Assessment> assessments = repository.getAllAssessments();

            runOnUiThread(() -> {
                ArrayAdapter<Term> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, terms);
                termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                courseSpinner.setAdapter(termAdapter);

                courseAdapter = new CourseAdapter(this, courses, assessments, (course, assessment) -> {
                    currentCourse = course;
                    courseIdEditTxt.setText(String.valueOf(course.getCourseId()));
                    courseNameEditTxt.setText(course.getTitle());
                    startDateEditTxt.setText(course.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    endDateEditTxt.setText(course.getEndDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    dCoursePhoneEditTxt.setText(course.getPhone());
                    dCnotesEditTxt.setText(course.getNotes());
                    dCourseInfoEditTxt.setText(course.getCourseInfo());
                    dCourseInstructorNameEditTxt.setText(course.getInstructorName());
                    dCourseEmailEditTxt.setText(course.getEmail());

                    for (int i = 0; i < courseStatusSpinner1.getCount(); i++) {
                        if (courseStatusSpinner1.getItemAtPosition(i).toString().equalsIgnoreCase(course.getStatus())) {
                            courseStatusSpinner1.setSelection(i);
                            break;
                        }
                    }

                    for (int j = 0; j < courseSpinner.getCount(); j++) {
                        Term term = (Term) courseSpinner.getItemAtPosition(j);
                        if (term.getTermId().equals(course.getTermId())) {
                            courseSpinner.setSelection(j);
                            break;
                        }
                    }
                });
                detailedCourseRecyView.setAdapter(courseAdapter);
            });
        }).start();
    }

    private void showDatePicker(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            c.set(y, m, d);
            String formatted = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(c.getTime());

            if (isStartDate) {
                startDateEditTxt.setText(formatted);
                startDateStr = formatted;
            } else {
                endDateEditTxt.setText(formatted);
                endDateStr = formatted;
            }
        }, year, month, day);

        dialog.show();
    }




private void scheduleNotification(boolean isStart) {
        String dateStr = isStart ? startDateEditTxt.getText().toString() : endDateEditTxt.getText().toString();
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
                String startStr = startDateEditTxt.getText().toString();
                if (!startStr.isEmpty()) {
                    Date start = sdf.parse(startStr);
                    if (start != null && selectedDate.before(start)) {
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            String message = "Course \"" + courseNameEditTxt.getText().toString().trim() +
                    (isStart ? "\" is starting today!" : "\" is ending today!");

            if (triggerTime > currentTime) {
                message = "Course \"" + courseNameEditTxt.getText().toString().trim() +
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
                    Toast.makeText(this, "Notification set", Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_LONG).show();
                }
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_course, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailedCourses.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.courseUpdate) {
            updateCourse();
            return true;
        }
        else if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        else if (id == R.id.courseDelete) {
            deleteCourse();
            return true;
        } else if (id == R.id.courseShareNotes) {
            String notes = dCnotesEditTxt.getText().toString().trim();
            if (notes.isEmpty()) {
                Toast.makeText(this, "No notes to share", Toast.LENGTH_SHORT).show();
            } else {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TITLE, "Course Notes");
                shareIntent.putExtra(Intent.EXTRA_TEXT, notes);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share Notes Via"));
            }
            return true;
        } else if (id == R.id.notifyStart) {
            scheduleNotification(true);
            return true;
        } else if (id == R.id.notifyEnd) {
            scheduleNotification(false);
            return true;
        } else if (id == R.id.updateCourseInfo) {
            String currentInfo = currentCourse.getCourseInfo();
            String newInfo = dCourseInfoEditTxt.getText().toString().trim();

            if (newInfo.equals(currentInfo)) {
                Toast.makeText(this, "Course info unchanged. Please change the info to update.", Toast.LENGTH_SHORT).show();
            } else {
                currentCourse.setCourseInfo(newInfo);
                repository.update(currentCourse);
                Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(DetailedCourses.this, Courses.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }, 1500);

            }
            return true;

        } else if (id == R.id.deleteCourseInfo) {
            currentCourse.setCourseInfo("");
            repository.update(currentCourse);
            dCourseInfoEditTxt.setText("");
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(DetailedCourses.this, Courses.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }, 1500);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateCourse() {
        if (currentCourse != null) {
            try {
                LocalDate startDate = LocalDate.parse(startDateEditTxt.getText().toString(), DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
                LocalDate endDate = LocalDate.parse(endDateEditTxt.getText().toString(), DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

                currentCourse.setTitle(courseNameEditTxt.getText().toString());
                currentCourse.setStartDate(startDate);
                currentCourse.setEndDate(endDate);
                currentCourse.setStatus(courseStatusSpinner1.getSelectedItem().toString());
                currentCourse.setPhone(dCoursePhoneEditTxt.getText().toString());
                currentCourse.setNotes(dCnotesEditTxt.getText().toString());
                currentCourse.setCourseInfo(dCourseInfoEditTxt.getText().toString());
                currentCourse.setInstructorName(dCourseInstructorNameEditTxt.getText().toString());
                currentCourse.setEmail(dCourseEmailEditTxt.getText().toString());

                Term selectedTerm = (Term) courseSpinner.getSelectedItem();
                if (selectedTerm != null && selectedTerm.getTermId() != null) {
                    currentCourse.setTermId(selectedTerm.getTermId());
                } else {
                    currentCourse.setTermId(null);
                }


                repository.update(currentCourse);
                courseAdapter.setCourses(repository.getAllCourses(), repository.getAllAssessments());

                Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(() -> finish(), 1500);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to update course", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No course selected to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCourse() {
        if (currentCourse != null) {
            try {
                repository.delete(currentCourse);
                courseAdapter.setCourses(repository.getAllCourses(), repository.getAllAssessments());
                Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
                currentCourse = null;
                new android.os.Handler().postDelayed(() -> finish(), 1500);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to delete course", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No course selected to delete", Toast.LENGTH_SHORT).show();
        }
    }
}
