package com.suyogbauskar.atten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FeedbackActivity extends AppCompatActivity {

    private TableLayout table;
    private boolean isFirstRow;
    private String studentDepartment;
    private int studentSemester;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        table = findViewById(R.id.table);
        isFirstRow = true;
        user = FirebaseAuth.getInstance().getCurrentUser();

        getAllStudentData();

        FirebaseDatabase.getInstance().getReference("feedback")
                .child(studentDepartment + studentSemester + "_feedback_started")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.getValue(Boolean.class)) {
                            new SweetAlertDialog(FeedbackActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Feedback form not started")
                                    .setContentText("Feedback form will appear when it's started")
                                    .show();
                        } else {
                            FirebaseDatabase.getInstance().getReference("students_data/" + user.getUid() + "/subjects/")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String subjectShortName;
                                            boolean feedbackSubmitted;
                                            drawTableHeader();

                                            for (DataSnapshot dsp : snapshot.getChildren()) {
                                                subjectShortName = dsp.child("subjectShortName").getValue(String.class);
                                                feedbackSubmitted = dsp.child("feedbackSubmitted").getValue(Boolean.class);

                                                if (feedbackSubmitted) {
                                                    createTableRow(subjectShortName, "✅", subjectShortName + "-" + "true");
                                                } else {
                                                    createTableRow(subjectShortName, "❌", subjectShortName + "-" + "false");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(FeedbackActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FeedbackActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAllStudentData() {
        SharedPreferences sh = getSharedPreferences("allDataPref", MODE_PRIVATE);
        studentSemester = sh.getInt("semester", 0);
        studentDepartment = sh.getString("department", "");
    }

    private void drawTableHeader() {
        TableRow tbRow = new TableRow(FeedbackActivity.this);

        TextView tv0 = new TextView(FeedbackActivity.this);
        TextView tv1 = new TextView(FeedbackActivity.this);

        tv0.setText("Subject");
        tv1.setText("Form Submitted");

        tv0.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTypeface(Typeface.DEFAULT_BOLD);

        tv0.setTextSize(18);
        tv1.setTextSize(18);

        tv0.setPadding(30, 30, 15, 30);
        tv1.setPadding(30, 30, 15, 30);

        tv0.setGravity(Gravity.CENTER);
        tv1.setGravity(Gravity.CENTER);

        tv0.setTextColor(Color.BLACK);
        tv1.setTextColor(Color.BLACK);

        tv0.setBackgroundColor(getResources().getColor(R.color.table_header));
        tv1.setBackgroundColor(getResources().getColor(R.color.table_header));

        tbRow.addView(tv0);
        tbRow.addView(tv1);

        table.addView(tbRow);
    }

    private void createTableRow(String subjectShortName, String formSubmitted, String values) {
        TableRow tbRow = new TableRow(FeedbackActivity.this);
        tbRow.setTag(values);

        TextView tv0 = new TextView(FeedbackActivity.this);
        TextView tv1 = new TextView(FeedbackActivity.this);

        tv0.setText(subjectShortName);
        tv1.setText(formSubmitted);

        tv0.setTextSize(16);
        tv1.setTextSize(16);

        tv0.setPadding(30, 30, 15, 30);
        tv1.setPadding(30, 30, 15, 30);

        tv0.setGravity(Gravity.CENTER);
        tv1.setGravity(Gravity.CENTER);

        tv0.setBackgroundResource(R.drawable.borders);
        tv1.setBackgroundResource(R.drawable.borders);

        tv0.setTextColor(Color.BLACK);
        tv1.setTextColor(Color.BLACK);

        if (isFirstRow) {
            tv0.setBackgroundColor(getResources().getColor(R.color.white));
            tv1.setBackgroundColor(getResources().getColor(R.color.white));
            isFirstRow = false;
        } else {
            tv0.setBackgroundColor(getResources().getColor(R.color.light_gray));
            tv1.setBackgroundColor(getResources().getColor(R.color.light_gray));
            isFirstRow = true;
        }

        tbRow.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("FeedbackActivityPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            String[] value = tbRow.getTag().toString().split("-");
            myEdit.putString("subjectShortName", value[0]);
            myEdit.commit();
            if (value[1].equals("false")) {
                startActivity(new Intent(FeedbackActivity.this, QuestionActivity.class));
            }
        });

        tbRow.addView(tv0);
        tbRow.addView(tv1);

        table.addView(tbRow);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FeedbackActivity.this, HomeActivity.class));
    }
}