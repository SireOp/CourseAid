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
import com.example.myCourse.entities.Course;
import com.example.myCourse.entities.Term;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<Term> mTerm;
    private List<Course> mCourse;
    private final Context context;
    private final LayoutInflater mInflater;
    private final OnTermClickListener listener;

    public interface OnTermClickListener {
        void OnTermClick(Term term, Course course);
    }

    public TermAdapter(Context context, List<Term> mTerm, List<Course> mCourse, OnTermClickListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mTerm = mTerm;
        this.mCourse = mCourse;
        this.listener = listener;
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        private final TextView termViewList;
        private final TextView termViewList1;

        private TermViewHolder(@NonNull View itemView, OnTermClickListener listener, List<Term> mTerm, List<Course> mCourse) {
            super(itemView);
            termViewList = itemView.findViewById(R.id.termRecyList);
            termViewList1 = itemView.findViewById(R.id.termRecyList1);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null && mTerm != null && position < mTerm.size()) {
                    try {
                        Term selectedTerm = mTerm.get(position);
                        Course selectedCourse = null;

                        if (mCourse != null && selectedTerm.getTermId() != null) {
                            for (Course c : mCourse) {
                                if (c.getTermId() != null && c.getTermId().equals(selectedTerm.getTermId())) {
                                    selectedCourse = c;
                                    break;
                                }
                            }
                        }

                        listener.OnTermClick(selectedTerm, selectedCourse);
                    } catch (Exception e) {
                        Log.e("TermAdapter", "Exception in ViewHolder click", e);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.termviewlist, parent, false);
        return new TermViewHolder(itemView, listener, mTerm, mCourse);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        if (mTerm == null || position >= mTerm.size()) {
            holder.termViewList.setText("No Terms available");
            holder.termViewList1.setText("");
            return;
        }

        Term current = mTerm.get(position);
        Integer termId = current.getTermId();
        String termName = current.getTermName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String startDate = current.getStartDate().format(formatter);
        String endDate = current.getEndDate().format(formatter);

        holder.termViewList.setText("Term Id: " + termId +
                "\nTerm Name: " + termName +
                "\nStart Date: " + startDate +
                "\nEnd Date: " + endDate);

        StringBuilder relatedCoursesText = new StringBuilder("Courses:\n");

        if (mCourse != null && termId != null) {
            for (Course course : mCourse) {
                if (course.getTermId() != null && course.getTermId().equals(termId)) {
                    relatedCoursesText.append("Course Id: ").append(course.getCourseId())
                            .append("\nCourse Name: ").append(course.getTitle()).append("\n\n");
                }
            }
        }

        if (relatedCoursesText.toString().equals("Courses:\n")) {
            relatedCoursesText.append("No courses available for this term.");
        }

        holder.termViewList1.setText(relatedCoursesText.toString());
    }

    @Override
    public int getItemCount() {
        return mTerm != null ? mTerm.size() : 0;
    }

    public void updateData(List<Term> terms, List<Course> courses){
        this.mTerm = terms;
        this.mCourse = courses;
        notifyDataSetChanged();

    }

    public void setTerms(List<Term> terms) {
        // guard against null
        this.mTerm = terms != null ? terms : new ArrayList<>();
        notifyDataSetChanged();
    }


    public void setCourses(List<Course> courses) {
        // guard against null
        this.mCourse = courses != null ? courses : new ArrayList<>();
        notifyDataSetChanged();
    }
}
