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
import com.example.task2.entities.Term;

import java.time.format.DateTimeFormatter;
import java.util.List;
public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<Term> mTerm;

    private  List<Course> mCourse;

    private  final Context context;

    private final LayoutInflater mInflater;


    public TermAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.context = context;
       //this.mCourse = courses;
    }


    public  class TermViewHolder extends RecyclerView.ViewHolder{

        private  final TextView termViewList;

        private final TextView termViewList1;
        private TermViewHolder(@NonNull View itemView) {
            super(itemView);
            termViewList = itemView.findViewById(R.id.termViewList);
            termViewList1 = itemView.findViewById(R.id.termViewList1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();



                    if (position != RecyclerView.NO_POSITION) ;
                    {
                        final Term current = mTerm.get(position);
                        Intent intent = new Intent(context, DetailedTerms.class);
                        intent.putExtra("termId",current.getTermId());
                        intent.putExtra("termName",current.getTermName());
                        intent.putExtra("startDate",current.getStartDate());
                        intent.putExtra("endDate", current.getEndDate());
                        intent.putExtra("courseName",current.getCourseName());
                        context.startActivity(intent);

                    }
                }
            });
        }


    }

    @NonNull
    @Override
    public TermAdapter.TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType){
        View itemView = mInflater.inflate(R.layout.activity_detailed_terms,parent, false);
        return  new TermViewHolder(itemView);

    }

    /*
    If the view holder is clicked on it takes the data and sends it to the next screen
    Inflate gets us the context

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.activity_detailed_terms, parent, false);
        return new TermViewHolder(itemView);
    }

     */
//This is where we put what we display on the recyclerView
// 1:03 start here
    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    if(mTerm != null){
        Term current = mTerm.get(position);
        int id = current.getTermId();
        String termName = current.getTermName();
        String startDate = current.getStartDate().format(df);
        String endDate = current.getEndDate().format(df);
        int courseId = current.getCourseId();
        String courseName = current.getCourseName();
        holder.termViewList.setText("Term Id: "+ id+ "\nTerm: "+ termName+ "\nStart Date: "+startDate +
                "\nEnd Date: "+ endDate + "\nCourse: "+ courseId+"\nCourse: "+ courseName);

        holder.termViewList1.setText("Term Id: "+ Integer.toString(id));
    }
    else {
        holder.termViewList.setText("No Terms available");
    }



    }






public void setTerms(List<Term> terms){
        mTerm = terms;
        notifyDataSetChanged();
}

    @Override
    public int getItemCount() {
        if (mTerm != null) {
            return mTerm.size();
        } else return 0;
    }


    public int getItemCount1() {
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
