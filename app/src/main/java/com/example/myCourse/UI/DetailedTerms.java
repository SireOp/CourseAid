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
import com.example.myCourse.database.Repository;
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailedTerms extends AppCompatActivity {

    private Spinner courseSpinner2;
    private EditText termNameEditText, termIdEditText, termStartDateTxt, termEndDateTxt;
    private Repository repository;
    private Integer termId;
    private Term currentTerm;
    private String startDateDTerms, endDateDTerms;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_terms);

        repository = new Repository(getApplication());

        termNameEditText = findViewById(R.id.detailedTermNameEditText);
        termIdEditText = findViewById(R.id.termIdTextView);
        termStartDateTxt = findViewById(R.id.termEditStartDate);
        termEndDateTxt = findViewById(R.id.termEditEndDate);
        courseSpinner2 = findViewById(R.id.courseSpinner2);
        recyclerView = findViewById(R.id.detailedTermsRecyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        termStartDateTxt.setFocusable(false);
        termEndDateTxt.setFocusable(false);
        termStartDateTxt.setOnClickListener(v -> seeDatePickerDialog(true));
        termEndDateTxt.setOnClickListener(v -> seeDatePickerDialog(false));

        refreshRecyclerView();

        Intent intent = getIntent();
        String termName = intent.getStringExtra("termName");
        String sDate = intent.getStringExtra("startDate");
        String eDate = intent.getStringExtra("endDate");
        termId = intent.hasExtra("id") ? intent.getIntExtra("id", -1) : null;

        termNameEditText.setText(termName);
        termIdEditText.setText(termId != null && termId != -1 ? String.valueOf(termId) : "");

        if (sDate != null) {
            termStartDateTxt.setText(sDate);
            startDateDTerms = sDate;
        }
        if (eDate != null) {
            termEndDateTxt.setText(eDate);
            endDateDTerms = eDate;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FloatingActionButton detailedTFloatBtn = findViewById(R.id.DetailedTFloatBtn);
        detailedTFloatBtn.setOnClickListener(v -> {
            Intent i = new Intent(DetailedTerms.this, Terms.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        });
    }

    private void refreshRecyclerView() {
        new Thread(() -> {
            List<Term> allTerms = repository.getAllTerms();
            List<Course> allCourses = repository.getAllCourses();

            runOnUiThread(() -> {
                ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allCourses);
                courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                courseSpinner2.setAdapter(courseAdapter);

                TermAdapter termAdapter = new TermAdapter(this, allTerms, allCourses, (term, course) -> {
                    currentTerm = term;
                    termIdEditText.setText(String.valueOf(term.getTermId()));
                    termNameEditText.setText(term.getTermName());
                    termStartDateTxt.setText(term.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    termEndDateTxt.setText(term.getEndDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

                    for (int i = 0; i < courseSpinner2.getCount(); i++) {
                        Course c = (Course) courseSpinner2.getItemAtPosition(i);
                        if (c.getTermId() != null && c.getTermId().equals(term.getTermId())) {
                            courseSpinner2.setSelection(i);
                            break;
                        }
                    }
                });
                recyclerView.setAdapter(termAdapter);
            });
        }).start();
    }

    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            c.set(Calendar.YEAR, y);
            c.set(Calendar.MONTH, m);
            c.set(Calendar.DAY_OF_MONTH, d);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(c.getTime());

            if (isStartDate) {
                termStartDateTxt.setText(formattedDate);
                startDateDTerms = formattedDate;
            } else {
                termEndDateTxt.setText(formattedDate);
                endDateDTerms = formattedDate;
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean isValidDate(String startStr, String endStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
            LocalDate start = LocalDate.parse(startStr, formatter);
            LocalDate end = LocalDate.parse(endStr, formatter);
            LocalDate startOfYear = LocalDate.of(2025, 1, 1);
            LocalDate endOfYear = LocalDate.of(2026, 12, 31);
            return !start.isBefore(startOfYear) && !end.isAfter(endOfYear) && !start.isAfter(end);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void validateAndSave() {
        String termName = termNameEditText.getText().toString().trim();
        String startStr = termStartDateTxt.getText().toString().trim();
        String endStr = termEndDateTxt.getText().toString().trim();

        if (termName.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
            Toast.makeText(this, "Enter valid name and dates", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidDate(startStr, endStr)) {
            Toast.makeText(this, "Dates must be within 2025 and valid", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
            LocalDate start = LocalDate.parse(startStr, formatter);
            LocalDate end = LocalDate.parse(endStr, formatter);

            List<Term> allTerms = repository.getAllTerms();
            Integer lastId = null;
            if (allTerms != null && !allTerms.isEmpty()) {
                lastId = allTerms.get(allTerms.size() - 1).getTermId();
            }
            Integer newId = (lastId != null ? lastId : 0) + 1;

            Term newTerm = new Term(newId, termName, start, end);
            repository.insert(newTerm);
            Toast.makeText(this, "Term saved", Toast.LENGTH_SHORT).show();
            refreshRecyclerView();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTerm() {
        if (currentTerm == null) {
            Toast.makeText(this, "No term selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = termNameEditText.getText().toString().trim();
        String sDate = termStartDateTxt.getText().toString().trim();
        String eDate = termEndDateTxt.getText().toString().trim();

        if (!isValidDate(sDate, eDate)) {
            Toast.makeText(this, "Invalid dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
            currentTerm.setTermName(name);
            currentTerm.setStartDate(LocalDate.parse(sDate, df));
            currentTerm.setEndDate(LocalDate.parse(eDate, df));
            repository.update(currentTerm);

            Course selectedCourse = (Course) courseSpinner2.getSelectedItem();
            if (selectedCourse != null && currentTerm.getTermId() != null) {
                selectedCourse.setTermId(currentTerm.getTermId());
                repository.update(selectedCourse);
            }

            Toast.makeText(this, "Term updated", Toast.LENGTH_SHORT).show();
            refreshRecyclerView();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTerm() {
        if (currentTerm == null || currentTerm.getTermId() == null) {
            Toast.makeText(this, "No term selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int numCourses = 0;
        for (Course c : repository.getAllCourses()) {
            if (Objects.equals(c.getTermId(), currentTerm.getTermId())) {
                numCourses++;
            }
        }

        if (numCourses > 0) {
            Toast.makeText(this, "Cannot delete term with courses", Toast.LENGTH_SHORT).show();
        } else {
            repository.delete(currentTerm);
            Toast.makeText(this, "Term deleted", Toast.LENGTH_SHORT).show();
            refreshRecyclerView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_term, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.termUpdate) {
            updateTerm();
            return true;
        } else if (item.getItemId() == R.id.termDelete) {
            deleteTerm();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailedTerms.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
