package com.example.myCourse.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
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

import com.example.myCourse.dao.DSM;
import com.example.myCourse.R;
import com.example.myCourse.database.Repository;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Assessments extends AppCompatActivity {

    private Spinner assessmentCourseSpinner, typeSpinner;
    private RecyclerView recyclerView;
    private AssessmentAdapter assessmentAdapter;
    private Repository repository;
    private EditText assessmentTitle, assessmentStartDate, assessmentEndDate;
    private String startDateAssess, endDateAssess;
    private int assessmentId;
    private DSM dsm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assessments);

        repository = new Repository(getApplication());
        dsm = new DSM(getApplication());
        assessmentCourseSpinner = findViewById(R.id.assessmentCourseSpinner);
        recyclerView = findViewById(R.id.AssessmentRecyView);
        assessmentTitle = findViewById(R.id.assessmentTitleEditTxt);
        assessmentStartDate = findViewById(R.id.startDateEditTxt);
        assessmentEndDate = findViewById(R.id.endDateTxt);
        typeSpinner = findViewById(R.id.typeSpinner);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"None", "Performance", "Objective"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        assessmentStartDate.setOnClickListener(v -> seeDatePickerDialog(true));
        assessmentEndDate.setOnClickListener(v -> seeDatePickerDialog(false));

        new Thread(() -> {
            List<Course> courses = repository.getAllCourses();
            List<Assessment> assessments = repository.getAllAssessments();
            Context context = Assessments.this;

            runOnUiThread(() -> {
                if (courses != null) {
                    ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, courses);
                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    assessmentCourseSpinner.setAdapter(courseAdapter);
                }

                AssessmentAdapter.OnAssessmentClickListener listener = assessment -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                        Intent intent = new Intent(context, DetailedAssessments.class);
                        intent.putExtra("assessmentId", assessment.getAssessmentId());
                        intent.putExtra("assessmentTitle", assessment.getAssessmentTitle());
                        intent.putExtra("type", assessment.getType());
                        intent.putExtra("startDate", assessment.getStartDate().format(formatter));
                        intent.putExtra("endDate", assessment.getEndDate().format(formatter));
                        intent.putExtra("courseId", assessment.getCourseId());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error opening assessment", Toast.LENGTH_SHORT).show();
                    }
                };

                assessmentAdapter = new AssessmentAdapter(context, assessments, listener);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(assessmentAdapter);
            });
        }).start();

        FloatingActionButton fab = findViewById(R.id.assessmentFloatingActionBtn);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Assessments.this, DetailedAssessments.class);
            intent.putExtra("test1", "Info sent");
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assessmentId = getIntent().getIntExtra("assessmentId", -1);
        String title = getIntent().getStringExtra("assessmentTitle");
        String sDate = getIntent().getStringExtra("startDate");
        String eDate = getIntent().getStringExtra("endDate");
        String type = getIntent().getStringExtra("type");

        if (title != null) assessmentTitle.setText(title);
        if (sDate != null) assessmentStartDate.setText(sDate);
        if (eDate != null) assessmentEndDate.setText(eDate);
        if (type != null) {
            int pos = typeAdapter.getPosition(type);
            if (pos >= 0) typeSpinner.setSelection(pos);
        }
    }

    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            c.set(y, m, d);
            String formatted = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(c.getTime());
            try {
                if (isStartDate) {
                    startDateAssess = formatted;
                    assessmentStartDate.setText(startDateAssess);
                } else {
                    endDateAssess = formatted;
                    assessmentEndDate.setText(endDateAssess);
                }

                if (startDateAssess != null && endDateAssess != null) {
                    Toast.makeText(this, "Assessment dates saved", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_LONG).show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void refreshRecyclerView() {
        new Thread(() -> {
            List<Assessment> updatedList = repository.getAllAssessments();
            runOnUiThread(() -> {
                assessmentAdapter = new AssessmentAdapter(this, updatedList, assessment -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                    Intent intent = new Intent(this, DetailedAssessments.class);
                    intent.putExtra("assessmentId", assessment.getAssessmentId());
                    intent.putExtra("assessmentTitle", assessment.getAssessmentTitle());
                    intent.putExtra("type", assessment.getType());
                    intent.putExtra("startDate", assessment.getStartDate().format(formatter));
                    intent.putExtra("endDate", assessment.getEndDate().format(formatter));
                    intent.putExtra("courseId", assessment.getCourseId());
                    startActivity(intent);
                });
                recyclerView.setAdapter(assessmentAdapter);
            });
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessments_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addAssessment) {
            saveAssessment();
            return true;
        } else if (item.getItemId() == R.id.deleteAssessment) {
            if (assessmentId != -1) {
                Assessment toDelete = new Assessment(assessmentId, assessmentTitle.getText().toString().trim(), typeSpinner.getSelectedItem().toString(), null, null, -1);
                repository.delete(toDelete);
                Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();
                refreshRecyclerView();
            } else {
                Toast.makeText(this, "No assessment selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAssessment() {
        String title = assessmentTitle.getText().toString().trim();
        String selectedType = typeSpinner.getSelectedItem().toString();
        String startStr = assessmentStartDate.getText().toString().trim();
        String endStr = assessmentEndDate.getText().toString().trim();

        Course selectedCourse = (Course) assessmentCourseSpinner.getSelectedItem();
        int courseId = selectedCourse != null ? selectedCourse.getCourseId() : -1;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
        LocalDate start = null, end = null;

        if (!selectedType.equals("None") && (startStr.isEmpty() || endStr.isEmpty())) {
            Toast.makeText(this, "Start and End date required", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if (!startStr.isEmpty()) start = LocalDate.parse(startStr, formatter);
            if (!endStr.isEmpty()) end = LocalDate.parse(endStr, formatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (assessmentId == -1) {
            int newId = repository.getAllAssessments().isEmpty()
                    ? 1
                    : repository.getAllAssessments().get(repository.getAllAssessments().size() - 1).getAssessmentId() + 1;

            Assessment newA = new Assessment(newId, title, selectedType, start, end, courseId);
            repository.insert(newA);

            scheduleAlarms(title, start, end);
            Toast.makeText(this, "Assessment created", Toast.LENGTH_SHORT).show();
            dsm.pushAssessment(newA, () -> runOnUiThread(this::refreshRecyclerView));
        } else {
            Assessment updated = new Assessment(assessmentId, title, selectedType, start, end, courseId);
            repository.update(updated);
            Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
            dsm.pushAssessment(updated, () -> runOnUiThread(this::refreshRecyclerView));
        }

        //refreshRecyclerView();
    }

    private void scheduleAlarms(String title, LocalDate start, LocalDate end) {
        if (start != null) {
            long trigger = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("key", "Assessment: " + title + " is starting!");
            PendingIntent sender = PendingIntent.getBroadcast(this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, trigger, sender);
        }

        if (end != null) {
            long trigger = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("key", "Assessment: " + title + " is ending!");
            PendingIntent sender = PendingIntent.getBroadcast(this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, trigger, sender);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerView();
    }
}
