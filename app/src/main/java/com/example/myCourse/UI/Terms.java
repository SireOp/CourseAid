package com.example.myCourse.UI;

import android.app.DatePickerDialog;
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
import com.example.myCourse.dao.DSM;
import com.example.myCourse.database.Repository;
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

public class Terms extends AppCompatActivity {



    private Repository repository;
    private DSM dsm;
    private Term currentTerm;

    private EditText termN, termStartDate, termEndDate;
    private Spinner courseSpinnerInTerm;
    private String startDateTerms, endDateTerms;
    private RecyclerView recyclerView;
    private TermAdapter termAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms);

        repository = new Repository(getApplication());
        dsm = new DSM(getApplication());

        termN = findViewById(R.id.termNameEditTxt);
        termStartDate = findViewById(R.id.termStartDateEditTxt);
        termEndDate = findViewById(R.id.termEndDateEditTxt);
        courseSpinnerInTerm = findViewById(R.id.courseSpinnerInTerm);

        termStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        termEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        recyclerView = findViewById(R.id.TermsRecyView);
        TermAdapter.OnTermClickListener listener = (term, course) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            Intent intent = new Intent(Terms.this, DetailedTerms.class);
            intent.putExtra("id", term.getTermId());
            intent.putExtra("termName", term.getTermName());
            intent.putExtra("startDate", term.getStartDate().format(formatter));
            intent.putExtra("endDate", term.getEndDate().format(formatter));
            startActivity(intent);
        };
        termAdapter = new TermAdapter(this, new ArrayList<>(), new ArrayList<>(), listener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(termAdapter);
        refreshRecyclerView();

        FloatingActionButton termsFloatBtn = findViewById(R.id.floatingActionBtnTerms);
        termsFloatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Terms.this, DetailedTerms.class);
            intent.putExtra("test1", "Info sent");
            startActivity(intent);
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        dsm.importDataFromServer(() -> runOnUiThread(() -> {
            // 1) pull what’s in Room now
            List<Term> allTerms = repository.getAllTerms();

            // 2) log it
            Log.d("TermsActivity", "After sync, Room has "
                    + allTerms.size() + " terms:");
            for (Term t : allTerms) {
                Log.d("TermsActivity",
                        "  • ID=" + t.getTermId()
                                + " Name=\"" + t.getTermName() + "\""
                                + " [" + t.getStartDate() + "→" + t.getEndDate() + "]");
            }

            // 3) optional toast
            Toast.makeText(this,
                    "Loaded " + allTerms.size() + " terms",
                    Toast.LENGTH_SHORT).show();

            // 4) now refresh your list
            setupCourseSpinner(repository.getAllCourses());
            refreshRecyclerView();
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sync data on resume
        dsm.importDataFromServer(() -> runOnUiThread(() -> {
            // 1) pull what’s in Room now
            List<Term> allTerms = repository.getAllTerms();

            // 2) log it
            Log.d("TermsActivity", "After sync, Room has "
                    + allTerms.size() + " terms:");
            for (Term t : allTerms) {
                Log.d("TermsActivity",
                        "  • ID=" + t.getTermId()
                                + " Name=\"" + t.getTermName() + "\""
                                + " [" + t.getStartDate() + "→" + t.getEndDate() + "]");
            }

            // 3) optional toast
            Toast.makeText(this,
                    "Loaded " + allTerms.size() + " terms",
                    Toast.LENGTH_SHORT).show();

            // 4) now refresh your list
            setupCourseSpinner(repository.getAllCourses());
            refreshRecyclerView();
        }));

        dsm.importDataFromServer(this::refreshRecyclerView);
    }



    private void setupCourseSpinner(List<Course> allCourses) {
        Course blankCourse = new Course(0, "", null, null, "", "", "",  "", "", "", -1);
        allCourses.add(0, blankCourse);
        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allCourses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinnerInTerm.setAdapter(adapter);
    }

    private void showDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            c.set(y, m, d);
            String formatted = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(c.getTime());
            if (isStartDate) {
                startDateTerms = formatted;
                termStartDate.setText(formatted);
            } else {
                endDateTerms = formatted;
                termEndDate.setText(formatted);
            }
        }, year, month, day);
        dialog.show();
    }

    private boolean isValidDate(String startStr, String endStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
        try {
            LocalDate s = LocalDate.parse(startStr, df);
            LocalDate e = LocalDate.parse(endStr, df);
            return !s.isAfter(e);
        } catch (Exception e) {
            return false;
        }
    }

    private void validateAndSave() {
        String name = termN.getText().toString().trim();
        String start = termStartDate.getText().toString().trim();
        String end = termEndDate.getText().toString().trim();

        if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(start, end)) {
            Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault());
            try {
                LocalDate startDate = LocalDate.parse(start, df);
                LocalDate endDate = LocalDate.parse(end, df);

                int termId = repository.getAllTerms().isEmpty() ? 1 :
                        repository.getAllTerms().get(repository.getAllTerms().size() - 1).getTermId() + 1;

                Term term = new Term(termId, name, startDate, endDate);
                repository.insert(term);

                Course selectedCourse = (Course) courseSpinnerInTerm.getSelectedItem();
                if (selectedCourse != null && selectedCourse.getCourseId() != 0) {
                    selectedCourse.setTermId(termId);
                    repository.update(selectedCourse);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Term saved", Toast.LENGTH_SHORT).show();
                    dsm.pushTerm(term, () -> {
                        Log.d("TermsActivity", "→ pushTerm completed for ID=" + term.getTermId());
                        dsm.importDataFromServer(() -> runOnUiThread(() -> {
                            setupCourseSpinner(repository.getAllCourses());
                            refreshRecyclerView();
                        }));
                    });
                });
            } catch (DateTimeParseException e) {
                runOnUiThread(() -> Toast.makeText(this, "Date format error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_list, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Terms.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveTerm) {
            Log.d("DEBUG", "Term startDate: " + startDateTerms);
            Log.d("DEBUG", "Term endDate: " + endDateTerms);

            validateAndSave();


            return true;
        } else if (id == R.id.deleteTerm) {
            deleteTerm();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTerm() {
        if (currentTerm == null) {
            Toast.makeText(this, "No term selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int numCourses = 0;
        for (Course c : repository.getAllCourses()) {
            if (c.getTermId() == currentTerm.getTermId()) ++numCourses;
        }

        if (numCourses > 0) {
            Toast.makeText(this, "Cannot delete term with courses", Toast.LENGTH_SHORT).show();
        } else {
            repository.delete(currentTerm);
            Toast.makeText(this, "Term deleted", Toast.LENGTH_SHORT).show();
            refreshRecyclerView();
        }
        Toast.makeText(this, "Term deleted", Toast.LENGTH_SHORT).show();
        refreshRecyclerView();
    }





    private void refreshRecyclerView() {
        List<Term> updatedTerms = repository.getAllTerms();
        List<Course> updatedCourses = repository.getAllCourses();
        //termAdapter.updateData(updatedTerms, updatedCourses);
        termAdapter.setTerms(updatedTerms);
        termAdapter.setCourses(updatedCourses);
    }



}




