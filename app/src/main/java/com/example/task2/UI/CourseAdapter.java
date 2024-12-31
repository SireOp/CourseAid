package com.example.task2.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task2.R;
import com.example.task2.entities.Course;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

private List<Course> mCourse;

private final Context context;

private final LayoutInflater mInflater;

// Gets context form the layout inflater
public CourseAdapter(Context context){
    mInflater = LayoutInflater.from(context);
    this.context = context;
}

    public class CourseViewHolder extends RecyclerView.ViewHolder  {

        private  final TextView courseViewList;
        private  final TextView courseViewList1;

        //All the items that make up a course
        private CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseViewList=itemView.findViewById(R.id.courseViewList);
            courseViewList1 = itemView.findViewById(R.id.courseViewList1);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public  void onClick(View view){
                    int position = getAdapterPosition();
                    final Course current =mCourse.get(position);
                    Intent intent = new Intent(context, DetailedCourses.class);
                    intent.putExtra("id", current.getCourseId());
                    intent.putExtra("title",current.getTitle());
                    intent.putExtra("startDate", current.getStartDate());
                    intent.putExtra("endDate", current.getEndDate());
                    intent.putExtra("status",current.getStatus());
                    intent.putExtra("assessment",current.getAssessment());
                    intent.putExtra("notes",current.getNotes());
                    intent.putExtra("courseInfo",current.getCourseInfo());
                    intent.putExtra("instructorId",current.getInstructorId());
                    intent.putExtra("instructorName", current.getInstructorName());
                    intent.putExtra("phone",current.getPhone());
                    intent.putExtra("email",current.getEmail());
                    //takes us to the next page
                    context.startActivity(intent);

                }
            });
        }

    }

    //Will inflate the Course list tem that has the textview on it
    @NonNull
    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.activity_detailed_courses, parent, false);
        return new CourseViewHolder(itemView);
    }
//1:02:21 for two views on the same screen
    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder holder, int position) {
DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        if (mCourse != null){
            Course current = mCourse.get(position);
            int id = current.getCourseId();
            String title = current.getTitle();
            String startDate = current.getStartDate().format(df);
            String endDate = current.getEndDate().format(df);
            String status = current.getStatus();
            String assessment = current.getAssessment();
            String notes = current.getNotes();
            String courseInfo = current.getCourseInfo();
            int instructorId = current.getInstructorId();
            String instructorName = current.getInstructorName();
            String phone = current.getPhone();
            String email = current.getEmail();
            holder.courseViewList.setText("Course ID: "+id +"\nCourse Name: " + title + "\nStart Date: "+ startDate+"\nEnd Date: "+ endDate +"\nStatus: "+ status + "\nAssessment: "+assessment + "\nNotes: "+ notes +"\nCourse Information: "+ courseInfo + "\nInstructor ID: "+ instructorId+"\nInstructor Name: "+ instructorName+ "\nPhone: "+phone+ "\nEmail "+ email);
            holder.courseViewList1.setText("Course ID: "+ Integer.toString(id));



        }
        else {
            holder.courseViewList.setText("No course Title available ");
        }
    }

    @Override
    public int getItemCount() {
        if(mCourse!= null){
            return mCourse.size();
        }

       else return 0;
    }

   public void setCourses(List<Course> courses){
        mCourse = courses;
        notifyDataSetChanged();

   }

}
