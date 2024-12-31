package com.example.task2.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Terms extends AppCompatActivity {


    private Repository repository;

    private Repository repositoryC;
//57 vid 2
    EditText termN;

    private TextView selectedSDateTxtTerms, selectedEDateTxtTerms;

    private Button addTermBtn, startDateBtnTerms, endDateBtnTerms, removeTermBtn;

    private String startDateTerms, endDateTerms;

    private int year, month, day;


    ArrayList<Terms> termList;
    ArrayAdapter<Terms> termAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms);

        selectedSDateTxtTerms = findViewById(R.id.selectedSDateTxtTerms);
        selectedEDateTxtTerms = findViewById(R.id.selectedEDateTxtTerms);
        startDateBtnTerms = findViewById(R.id.startDateBtnTerms);
        endDateBtnTerms = findViewById(R.id.endDateBtnTerms);
        addTermBtn = findViewById(R.id.addTermBtn);
        removeTermBtn = findViewById(R.id.removeTermBtn);
        termN = findViewById(R.id.termNameEditTxt);

        startDateBtnTerms.setOnClickListener(v -> seeDatePickerDialog(true));
        endDateBtnTerms.setOnClickListener(v -> seeDatePickerDialog(false));
        System.out.println(getIntent().getStringExtra("test"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.TermsRecyView);
        repository = new Repository(getApplication());
        repositoryC = new Repository(getApplication());
        final TermAdapter termAdapter = new TermAdapter(this);
        recyclerView.setAdapter(termAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Term> allTerms = repository.getAllTerms();
        List<Course> allCoures = repositoryC.getRelatedCourse();
        termAdapter.setTerms(allTerms);
        termAdapter.setCourses(allCoures);
        /*
        List<Term> allTerms = repository.getAllTerms();
        termAdapter.setT


         */



        //termSelect = findViewById(R.id.termSelect);


        /*
        EditText termName = findViewById(R.id.termName);
        EditText termStart = findViewById(R.id.termStart);
        EditText termEnd = findViewById(R.id.termEnd);

        */

        //Default values for spinner list
        termList = new ArrayList<>();
/*
        //ArrayAdapter binding termList(ArrayList) to termSelect(Spinner)
        termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,termList);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSelect.setAdapter(termAdapter);
*/



        //Button click lister to add the user's created term Name start and End date
        addTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termName = termN.getText().toString().trim();


                //validation for for empty no values and validation for actual dates
                // Come back and add validation for calendar year dates for Start & end
                if (!termName.isEmpty() && isValidDate(startDateTerms, endDateTerms)) {
                    Terms newTerms = new Terms();
                    termList.add(newTerms);

                    //Tell adapter that data has changed
                    termAdapter.notifyDataSetChanged();

                    //Clear after date input
                    termN.setText("");
                    selectedSDateTxtTerms.setText("");
                    selectedEDateTxtTerms.setText("");
                } else {
                    Toast.makeText(Terms.this, "Enter a valid Term name and select start and end date ", Toast.LENGTH_LONG).show();
                }
//Place Listener & logic for deleting Term only if there are no courses tied to it
                    /*
                    removeTermBtn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view)
                    }
                        int courseCount = 0;

                    {
                    })
                    */
            }


            private boolean isValidDate(String startDateCheckStr, String endDateCheckStr) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                sdf.setLenient(false);

                try {
                    Date startDateCheck = sdf.parse(startDateCheckStr);
                    Date endDateCheck = sdf.parse(endDateCheckStr);

                    Date startOfYear = sdf.parse("01/01/2024");
                    Date endOfYear = sdf.parse("12/31/2024");

                    return startDateCheck != null && endDateCheck != null && !startDateCheck.before(startOfYear)
                            && !endDateCheck.after(endOfYear) && !startDateCheck.after(endDateCheck);
                } catch (ParseException e) {
                    return false;
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//1:17 is when the FAB is created
        //1:26 starts the menu set up to jump from page to page
        FloatingActionButton termsFloatBtn = findViewById(R.id.floatingActionBtnTerms);
        termsFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Terms.this, DetailedTerms.class);
                intent.putExtra("test1", "Info sent");
                startActivity(intent);
            }
        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
            getMenuInflater().inflate(R.menu.menu_term_list, menu);
            return  true;
    }


     */
    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


//Fix date
        DatePickerDialog datePickerDialog = new DatePickerDialog(Terms.this, (view, year1, monthOfYear, dayOfMonth) ->
        {
            c.set(Calendar.YEAR, year1);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

            String date = sdf.format(c.getTime());
            //year1 + "-" + (monthOfYear + 1) + "-"  + dayOfMonth;
            if (isStartDate) {
                startDateTerms = date;
                selectedSDateTxtTerms.setText("Term Start Date: " + startDateTerms);
            } else {
                endDateTerms = date;
                selectedEDateTxtTerms.setText("Term End Date; " + endDateTerms);
            }

            if (startDateTerms != null && endDateTerms != null) {
                // Place method to save to DB here
                Toast.makeText(Terms.this, "Dates have been saved", Toast.LENGTH_SHORT).show();
            }

        }, year, month, day);
        datePickerDialog.show();
    }


//1:33 most recent point

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_list, menu);
        return true;
    }



    LocalDate intTermStartDate = LocalDate.of(2024, 12, 15);
    LocalDate intTermEndDate = LocalDate.of(2024,03,19);

    LocalDate intCourseStartDate = LocalDate.of(2024, 12, 15);
    LocalDate intCourseEndDate = LocalDate.of(2024,03,19);

    //1:06 start here
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addIntTermCourse) {
            repository = new Repository(getApplication());
            //Toast.makeText(Terms.this, "sample", Toast.LENGTH_LONG).show();
            Term term = new Term(0,"Winter",intTermStartDate,intTermEndDate,"Biology 101",0);
            repository.insert(term);
            Course course = new Course(0,"Math 204",intCourseStartDate,intCourseEndDate,"in progress","Great","No notes","Advanced",1,"Mark Maclaurin","555-346-8821","Mmaclurin@gmail.com",0);
            repository.insert(course);
            return true;
        }
        //By default home is the main activity
        if (item.getItemId() == android.R.id.home) {
            this.finish();

            return true;
        }
        return false;
    }
}
        /*}
        Use this if you want the back arrow to take you some where else
        Intent intent = new Intent(Terms.this, DetailedTerms.class);
        This will take use from terms to DetailedTerms page, useful in the event that I saved a term and wanted
        To go back to the previous page so that another term could be saved, and the most recent saved term can be checked

         */
    //  return true;


    //return true;


