// src/com/example/myCourse/UI/CourseAdapter.java

package com.example.myCourse.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCourse.R;
import com.example.myCourse.entities.Assessment;
import com.example.myCourse.entities.Course;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying Course + its Assessments in a RecyclerView.
 * Now inflates R.layout.courseviewlist and uses the correct TextView IDs:
 *   - courseRecyList
 *   - courseRecyList1
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    /** Listener interface for clicks */
    public interface OnCourseClickListener {
        void onCourseClick(Course course, Assessment unusedAssessment);
    }

    private final Context mContext;
    private List<Course> mCourseList;             // list of courses to display
    private List<Assessment> mAssessmentList;     // full list of assessments
    private final OnCourseClickListener mListener;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public CourseAdapter(
            Context context,
            List<Course> courseList,
            List<Assessment> assessmentList,
            OnCourseClickListener listener
    ) {
        this.mContext = context;
        this.mCourseList = (courseList != null) ? courseList : new ArrayList<>();
        this.mAssessmentList = (assessmentList != null) ? assessmentList : new ArrayList<>();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (courseviewlist.xml)
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.courseviewlist, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course current = mCourseList.get(position);
        if (current == null) return;

        int id = current.getCourseId();
        String title = current.getTitle();
        String startDate = current.getStartDate().format(df);
        String endDate = current.getEndDate().format(df);
        String status = current.getStatus();
        String notes = current.getNotes();
        String courseInfo = current.getCourseInfo();
        String instructorName = current.getInstructorName();
        String phone = current.getPhone();
        String email = current.getEmail();
        Integer termId = current.getTermId();

        // Build a string of this course’s assessments
        StringBuilder assessmentsBuilder = new StringBuilder("Assessments:\n");
        boolean hasAssessment = false;
        if (mAssessmentList != null) {
            for (Assessment a : mAssessmentList) {
                if (a.getCourseId() == id) {
                    hasAssessment = true;
                    String aStart = a.getStartDate().format(df);
                    String aEnd = a.getEndDate().format(df);
                    assessmentsBuilder
                            .append("• ")
                            .append(a.getAssessmentTitle())
                            .append(" (")
                            .append(a.getType())
                            .append(")\n    Start: ")
                            .append(aStart)
                            .append("\n    End: ")
                            .append(aEnd)
                            .append("\n\n");
                }
            }
        }
        if (!hasAssessment) {
            assessmentsBuilder.append("No assessments available.");
        }

        // Populate the upper TextView (courseRecyList) with the course details
        String courseText =
                "Course ID: " + id +
                        "\nCourse Name: " + title +
                        "\nStart Date: " + startDate +
                        "\nEnd Date: " + endDate +
                        "\nStatus: " + status +
                        "\nNotes: " + notes +
                        "\nCourse Info: " + courseInfo +
                        "\nInstructor: " + instructorName +
                        "\nPhone: " + phone +
                        "\nEmail: " + email +
                        "\nTerm ID: " + termId;
        holder.courseViewTop.setText(courseText);

        // Populate the lower TextView (courseRecyList1) with its assessments
        holder.courseViewBottom.setText(assessmentsBuilder.toString());

        // Handle click: pass the Course (and an unused Assessment placeholder)
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCourseClick(current, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mCourseList != null) ? mCourseList.size() : 0;
    }

    /**
     * Replace both the course‐list and assessment‐list, then refresh the RecyclerView.
     */
    public void setCourses(List<Course> courses, List<Assessment> assessments) {
        this.mCourseList = (courses != null) ? courses : new ArrayList<>();
        this.mAssessmentList = (assessments != null) ? assessments : new ArrayList<>();
        notifyDataSetChanged();
    }

    // -------- ViewHolder class --------
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseViewTop;    // corresponds to R.id.courseRecyList
        TextView courseViewBottom; // corresponds to R.id.courseRecyList1

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseViewTop = itemView.findViewById(R.id.courseRecyList);
            courseViewBottom = itemView.findViewById(R.id.courseRecyList1);
        }
    }
}
