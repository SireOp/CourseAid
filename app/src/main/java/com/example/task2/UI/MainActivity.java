package com.example.task2.UI;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import android.content.Intent;
import android.widget.Button;

import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.task2.R;


public class MainActivity extends AppCompatActivity {

    private static String ip = "192.168.1.140";// this is the host ip that your data base exists on you can use 10.0.2.2 for local host                                                    found on your pc. use if config for windows to find the ip if the database exists on                                                    your pc
    private static String port = "5433";// the port sql server runs on
    private static String Classes = "org.postgresql.Driver";// the driver that is required for this connection use                                                                           "org.postgresql.Driver" for connecting to postgresql
    private static String database = "Task2DB";// the data base name
    private static String username = "postgres";// the user name
    private static String password = "RTMonster87";// the password
    private static String url = "jdbc:postgresql://"+ip+":"+port+"/"+database; // the connection url string

    private Connection connection = null;

    private TextView conStatTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        ///new DatabaseConnectionTask().execute();


        Button courseBtn=findViewById(R.id.courseBtn);
        courseBtn.setOnClickListener(view -> {

                /*
                 This first part is where we are the second part is where wer are going
                 separated with a coma
                 Intent intent=new  Intent(MainActivity.this,Courses.class);

                 */


                Intent intent=new  Intent(MainActivity.this,Courses.class);
                intent.putExtra("test", "Info sent");
                startActivity(intent);
            });



        Button termBtn = findViewById(R.id.termBtn);
        termBtn.setOnClickListener(view ->{

                Intent intent=new  Intent(MainActivity.this, Terms.class);
                intent.putExtra("test1", "Info sent");
                startActivity(intent);

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void start(View view) {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url, username,password);
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Connected no", Toast.LENGTH_SHORT).show();
        }
    }
/*
    private  class DatabaseConnectionTask extends AsyncTask<Void, Void, String>{


        @Override
        protected String doInBackground(Void... voids){
            Connection c = JDBC.getConnection();
            if(c != null){
                return "Connection successful!";
            } else{
                return "Connection failed";
            }
        }

         @Override
         protected  void onPostExecute(String result){
             conStatTextView.setText(result);

             JDBC.closeConnection();
         }


     }


 */


}