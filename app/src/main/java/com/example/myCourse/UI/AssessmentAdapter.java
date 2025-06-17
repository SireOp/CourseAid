package com.example.myCourse.UI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myCourse.R;

import com.example.myCourse.entities.Assessment;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private List<Assessment> mAssessment;
    private final OnAssessmentClickListener listener;
    private final Context context;
    private final LayoutInflater mInflater;

    public interface OnAssessmentClickListener {
        void onAssessmentClick(Assessment assessment);
    }

    public AssessmentAdapter(Context context, List<Assessment> mAssessment, OnAssessmentClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mAssessment = mAssessment;
        this.listener = listener;
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView assessmentViewList;

        public AssessmentViewHolder(@NonNull View itemView, OnAssessmentClickListener listener, List<Assessment> mAssessment) {
            super(itemView);
            assessmentViewList = itemView.findViewById(R.id.assessmentRecyList);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    try {
                        Assessment selectedAssessment = mAssessment.get(position);
                        Log.d("AssessmentAdapter", "Assessment Clicked - ID: " + selectedAssessment.getAssessmentId()
                                + " | Title: " + selectedAssessment.getAssessmentTitle());
                        listener.onAssessmentClick(selectedAssessment);
                    } catch (Exception e) {
                        Log.e("AssessmentAdapter", "Error in AssessmentViewHolder onClick", e);
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.assessment_list, parent, false);
        return new AssessmentViewHolder(itemView, listener, mAssessment);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        if (mAssessment != null) {
            Assessment current = mAssessment.get(position);
            int id = current.getAssessmentId();
            String title = current.getAssessmentTitle();
            String type = current.getType();
            String startDate = current.getStartDate().format(df);
            String endDate = current.getEndDate().format(df);
            int courseId = current.getCourseId();


            String displayText = "Assessment ID: " + id +
                    "\nTitle: " + title +
                    "\nType: " + type +
                    "\nStart Date: " + startDate +
                    "\nEnd Date: " + endDate +
                    "\nCourse ID: " + courseId;
            holder.assessmentViewList.setText(displayText);
        } else {
            holder.assessmentViewList.setText("No assessments available");
        }
    }

    @Override
    public int getItemCount() {
        return (mAssessment != null) ? mAssessment.size() : 0;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.mAssessment = assessments;
        notifyDataSetChanged();
    }
}
