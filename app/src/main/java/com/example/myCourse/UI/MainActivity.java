package com.example.myCourse.UI;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myCourse.R;
import com.example.myCourse.dao.DSM;
import com.example.myCourse.database.Repository;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Term;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int numAlert;
    private TextView conStatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DSM dsm   = new DSM(getApplication());
        Repository repo = new Repository(getApplication());

        // 1) Check if local Room database is empty:
        List<Term>       localTerms       = repo.getAllTerms();
        List<Course>     localCourses     = repo.getAllCourses();
        List<Assessment> localAssessments = repo.getAllAssessments();

        boolean isEmpty = (localTerms       == null || localTerms.isEmpty())
                && (localCourses     == null || localCourses.isEmpty())
                && (localAssessments == null || localAssessments.isEmpty());

        // 2) Always import from server on startup (no more bulk‐sync):
        if (isEmpty) {
            // First time → fresh load
            dsm.importDataFromServer(() -> runOnUiThread(() ->
                    Toast.makeText(MainActivity.this,
                            "Data loaded from server",
                            Toast.LENGTH_SHORT).show()
            ));
        } else {
            // Already had local data → just refresh
            dsm.importDataFromServer(() -> runOnUiThread(() ->
                    Toast.makeText(MainActivity.this,
                            "Fetched latest data from server",
                            Toast.LENGTH_SHORT).show()
            ));
        }

        // Notification channel for Android O+:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "alertChannel",
                    "Assessment Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Used for Assessment start/end alerts");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Exact alarms permission for Android S+:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this,
                        "Enable exact alarms to receive notifications",
                        Toast.LENGTH_LONG).show();
                Intent intent =
                        new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // Button navigation:
        Button courseBtn = findViewById(R.id.courseBtn);
        courseBtn.setOnClickListener(view ->
                startActivity(new Intent(
                        MainActivity.this, Courses.class))
        );

        Button termBtn = findViewById(R.id.termBtn);
        termBtn.setOnClickListener(view ->
                startActivity(new Intent(
                        MainActivity.this, Terms.class))
        );

        Button assessmentBtn = findViewById(R.id.assessmentBtn);
        assessmentBtn.setOnClickListener(view ->
                startActivity(new Intent(
                        MainActivity.this, Assessments.class))
        );

        // Edge-to-edge insets handling (optional):
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );
                    return insets;
                }
        );
    }
}
