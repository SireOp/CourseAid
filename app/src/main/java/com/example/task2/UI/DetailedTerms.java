package com.example.task2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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


public class DetailedTerms extends AppCompatActivity {


    String termName;

    int termId;

    int courseId;

    Term currentTerm;

    int numTerms;
    EditText termNameEditText;

    EditText termIdEditText;

    EditText termEditNotes;

    TextView termEditSDate;
    TextView termEditEDate;

    Repository repository;

    DatePickerDialog.OnDateSetListener startDate;

    private Button addTermBtnDetailed, startDateBtnDTerms, endDateBtnDTerms;

    private TextView selectedSDateDTxtTerms, selectedEDateDTxtTerms;

    private String startDateDTerms, endDateDTerms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_terms);
        repository = new Repository(getApplication());
        termName = getIntent().getStringExtra("termName");
        termNameEditText = findViewById(R.id.detailedTermNameEditText);
        termNameEditText.setText(termName);
        termId = getIntent().getIntExtra(String.valueOf(termId),-1);
        courseId = getIntent().getIntExtra(String.valueOf(courseId), -1);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });



        FloatingActionButton termsFloatBtn = findViewById(R.id.DetailedTFloatBtn);
        termsFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailedTerms.this, Terms.class);
                intent.putExtra("test1", "Info sent");
                startActivity(intent);
            }
        });


        //Ww get the repository here the term adaptor this is from the product list mine is
        RecyclerView recyclerView = findViewById(R.id.detailedTermsRecyView);

        final TermAdapter termAdapter = new TermAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(termAdapter);
        List<Term> allTerms = repository.getAllTerms();


        termAdapter.setTerms(allTerms);

/*
        RecyclerView detailedTermList = findViewById(R.id.detailedTermList);
        detailedTermList.setLayoutManager(new LinearLayoutManager(this));
        //detailedTermList.setAdapter(new termListAdapter(dataList));

 */
    }

    private void seeDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


//Fix date
        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailedTerms.this, (view, year1, monthOfYear, dayOfMonth) ->
        {
            c.set(Calendar.YEAR, year1);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

            String date = sdf.format(c.getTime());
            //year1 + "-" + (monthOfYear + 1) + "-"  + dayOfMonth;
            if (isStartDate) {
                startDateDTerms = date;
                selectedSDateDTxtTerms.setText("Term Start Date: " + startDateDTerms);
            } else {
                endDateDTerms = date;
                selectedEDateDTxtTerms.setText("Term End Date; " + endDateDTerms);
            }

            if (startDateDTerms != null && endDateDTerms != null) {
                // Place method to save to DB here
                Toast.makeText(DetailedTerms.this, "Dates have been saved", Toast.LENGTH_SHORT).show();
            }

        }, year, month, day);
        datePickerDialog.show();
    }


    public boolean onCreateOptionMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_detailed_term, menu);
        return  true;
    }

    /*
    We are going to set start date as the selected date from the date picker
    or the date from the selected item from the terms page

     */
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== R.id.termSave){
            //repository = new Repository((getApplication()));
            Term term;
            if(termId == -1){
                if(repository.getAllTerms().size() == 0) termId = 1;
                    //Will get the size of the list of course index of ) so we -1 makes sure the product Id are unique
                else termId = repository.getAllTerms().get(repository.getAllTerms().size() - 1).getTermId() + 1;
                term = new Term(termId, termNameEditText.getText().toString()), ;
                //                term = new Term(termId, termIdEditText.getText().toString(),Integer.parseInt(termIdEditText.getText().toString()));
                repository.insert(term);
                Toast.makeText(DetailedTerms.this,"Term was added.", Toast.LENGTH_LONG).show();
                this.finish();
            }
            else {
                term = new Term(termId, termNameEditText.getText().toString());
                repository.update(term);
                Toast.makeText(DetailedTerms.this,"Term was updated.", Toast.LENGTH_LONG).show();
                this.finish();
            }
            if(item.getItemId() == R.id.termDelete){
                for (Term ter:repository.getAllTerms()){
                    if (ter.getTermId() == termId)currentTerm = ter;
                }
                numTerms = 0;
                for (Course course: repository.getAllCourses()){
                    if(course.getTermId() == termId ) ++numTerms;
                }
            }   if (numTerms == 0){
                repository.delete(currentTerm);
                Toast.makeText(DetailedTerms.this, currentTerm.getTermName() + "was removed", Toast.LENGTH_LONG).show();
                DetailedTerms.this.finish();
            }
            else{
                Toast.makeText(DetailedTerms.this, "A Term can't be deleted until all courses are removed", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }





}