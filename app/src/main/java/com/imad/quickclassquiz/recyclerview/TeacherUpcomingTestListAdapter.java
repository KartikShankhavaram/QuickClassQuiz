package com.imad.quickclassquiz.recyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.QuestionListActivity;
import com.imad.quickclassquiz.activities.TeacherStartTestActivity;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.StaticValues;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherUpcomingTestListAdapter extends RecyclerView.Adapter<TeacherUpcomingTestListAdapter.TeacherUpcomingTestViewHolder> {

    private static OnTestVisibilityChangeListener onTestVisibilityChangeListener;
    private ArrayList<Test> testArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;
    private FirebaseFirestore firestore;

    public TeacherUpcomingTestListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TeacherUpcomingTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_upcoming_test_teacher, parent, false);
        JodaTimeAndroid.init(mContext);
        return new TeacherUpcomingTestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherUpcomingTestViewHolder holder, int position) {
        TextView testNameTextView = holder.getTestNameTextView();
        TextView testDesctextView = holder.getTestDescTextView();
        Button editTestButton = holder.getEditTestButton();
        Button startTestButton = holder.getStartTestButton();
        TextView testAddDateTextView = holder.getTestAddTimeTextView();
        Button testVisibilityToggleButton = holder.getTestVisibilityToggleButton();
        TextView questionCountTextView = holder.getQuestionCountTextView();

        ProgressDialog visibilityUpdateDialog = new ProgressDialog(mContext);
        visibilityUpdateDialog.setCanceledOnTouchOutside(false);
        visibilityUpdateDialog.setTitle("Updating visibility");

        Test test = testArrayList.get(position);

        if (!test.getVisible()) {
            startTestButton.setEnabled(false);
            testVisibilityToggleButton.setText("Make Test Public");
            editTestButton.setEnabled(true);
        } else {
            testVisibilityToggleButton.setText("Make Test Private");
            startTestButton.setEnabled(true);
            editTestButton.setEnabled(false);
        }

        String testName = test.getTestName();
        String testDesc = test.getTestDesc();

        testNameTextView.setText(testName);
        testDesctextView.setText(testDesc);

        String questionText = test.getQuestionCount() <= 1 ? " question" : " questions";
        String questionCount = Integer.toString(test.getQuestionCount());

        if (questionCount.equals("0"))
            questionCountTextView.setText("No questions");
        else {
            SpannableStringBuilder str = new SpannableStringBuilder(test.getQuestionCount() + questionText);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, questionCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionCountTextView.setText(str);
        }

        String timestamp = test.getCreatedAt();
        DateTime dt = new DateTime(timestamp);
        DateTimeFormatter format = DateTimeFormat.forPattern("'Added on 'MMM d' at 'h:mm a").withZone(DateTimeZone.forID("Asia/Kolkata"));
        String time = format.print(dt);
        testAddDateTextView.setText(time);

        editTestButton.setOnClickListener(v -> {
            Intent editTest = new Intent(mContext, QuestionListActivity.class);
            editTest.putExtra("test", test);
            editTest.putExtra("started", false);
            StaticValues.setCurrentTest(test);
            mContext.startActivity(editTest);
        });
        startTestButton.setOnClickListener(v -> {
                new NetworkUtils(internet -> {
                    if (internet) {
                        Intent startTest = new Intent(mContext, TeacherStartTestActivity.class);
                        startTest.putExtra("test", test);
                        startTest.putExtra("generated", false);
                        new AlertDialog.Builder(mContext)
                                .setCancelable(false)
                                .setTitle("Start test")
                                .setMessage(String.format("Are you sure you want to start the test '%s'?", testName))
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    mContext.startActivity(startTest);
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> {

                                })
                                .show();
                    } else {
                        Toast.makeText(mContext, "No internet available.", Toast.LENGTH_SHORT).show();
                    }
                });
        });
        testVisibilityToggleButton.setOnClickListener(v -> {
            if(!test.getVisible() && test.getQuestionCount() == 0) {
                Toast.makeText(mContext, "There are no questions in this test. Add a question to make this test public.", Toast.LENGTH_SHORT).show();
            }  else {
                new NetworkUtils(internet -> {
                    if (internet) {
                        Map<String, Object> visibility = new HashMap<>();
                        if (test.getVisible()) {
                            visibility.put("visible", false);
                            visibilityUpdateDialog.setMessage("Please wait while visibility is set to private...");
                        } else {
                            visibility.put("visible", true);
                            visibilityUpdateDialog.setMessage("Please wait while visibility is set to public...");
                        }
                        visibilityUpdateDialog.show();
                        firestore.document("tests/" + test.getTestId())
                                .update(visibility)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Updated successfully!", Toast.LENGTH_SHORT).show();
                                        onTestVisibilityChangeListener.onTestVisibilityChanged(test);
                                    } else {
                                        Toast.makeText(mContext, "Error in updating visibility.", Toast.LENGTH_SHORT).show();
                                    }
                                    visibilityUpdateDialog.dismiss();
                                });
                    } else {
                        Toast.makeText(mContext, "No internet available.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setListContent(List<Test> testArrayList) {
        final TestDiffCallback diffCallback = new TestDiffCallback(this.testArrayList, testArrayList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.testArrayList.clear();
        this.testArrayList.addAll(testArrayList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return testArrayList.size();
    }

    public void setOnTestVisibilityChangedListener(OnTestVisibilityChangeListener listener) {
        onTestVisibilityChangeListener = listener;
    }

    public interface OnTestVisibilityChangeListener {
        void onTestVisibilityChanged(Test test);
    }

    public class TeacherUpcomingTestViewHolder extends RecyclerView.ViewHolder {

        private TextView testNameTextView;
        private TextView testDescTextView;
        private Button editTestButton;
        private Button startTestButton;
        private TextView testAddTimeTextView;
        private Button testVisibilityToggleButton;
        private TextView questionCountTextView;

        public TeacherUpcomingTestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameTextView);
            testDescTextView = itemView.findViewById(R.id.testDescTextView);
            editTestButton = itemView.findViewById(R.id.testEditButton);
            startTestButton = itemView.findViewById(R.id.testStartButton);
            testAddTimeTextView = itemView.findViewById(R.id.testAddDateTextView);
            testVisibilityToggleButton = itemView.findViewById(R.id.testVisibilityToggleButton);
            questionCountTextView = itemView.findViewById(R.id.questionCountTextView);
        }

        public TextView getTestNameTextView() {
            return testNameTextView;
        }

        public TextView getTestDescTextView() {
            return testDescTextView;
        }

        public Button getEditTestButton() {
            return editTestButton;
        }

        public Button getStartTestButton() {
            return startTestButton;
        }

        public TextView getTestAddTimeTextView() {
            return testAddTimeTextView;
        }

        public Button getTestVisibilityToggleButton() {
            return testVisibilityToggleButton;
        }

        public TextView getQuestionCountTextView() {
            return questionCountTextView;
        }
    }
}
