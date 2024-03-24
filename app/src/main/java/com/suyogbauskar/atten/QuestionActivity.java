package com.suyogbauskar.atten;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuestionActivity extends AppCompatActivity {

    private FirebaseUser user;
    private EditText mobileNoEditText;
    private Button submitBtn;
    private RatingBar oneRating, twoRating, threeRating, fourRating, fiveRating, sixRating, sevenRating, eightRating, nineRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        SharedPreferences sh = getSharedPreferences("FeedbackActivityPref", MODE_PRIVATE);
        String subjectShortName = sh.getString("subjectShortName", "Feedback");
        setTitle(subjectShortName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mobileNoEditText = findViewById(R.id.mobileNo);
        submitBtn = findViewById(R.id.submitBtn);
        oneRating = findViewById(R.id.oneRating);
        twoRating = findViewById(R.id.twoRating);
        threeRating = findViewById(R.id.threeRating);
        fourRating = findViewById(R.id.fourRating);
        fiveRating = findViewById(R.id.fiveRating);
        sixRating = findViewById(R.id.sixRating);
        sevenRating = findViewById(R.id.sevenRating);
        eightRating = findViewById(R.id.eightRating);
        nineRating = findViewById(R.id.nineRating);

        user = FirebaseAuth.getInstance().getCurrentUser();

        submitBtn.setOnClickListener(v -> {
            String mobileNo = mobileNoEditText.getText().toString().trim();

            if (!Pattern.matches("^[0-9]{10}$", mobileNo)) {
                Toast.makeText(QuestionActivity.this, "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseDatabase.getInstance().getReference("students_data/" + user.getUid() + "/subjects/")
                        .orderByChild("subjectShortName")
                        .equalTo(subjectShortName)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dsp: snapshot.getChildren()) {
                                    dsp.getRef().child("mobileNo").setValue(Integer.parseInt(mobileNo));
                                    dsp.getRef().child("oneRating").setValue(oneRating.getRating());
                                    dsp.getRef().child("twoRating").setValue(twoRating.getRating());
                                    dsp.getRef().child("threeRating").setValue(threeRating.getRating());
                                    dsp.getRef().child("fourRating").setValue(fourRating.getRating());
                                    dsp.getRef().child("fiveRating").setValue(fiveRating.getRating());
                                    dsp.getRef().child("sixRating").setValue(sixRating.getRating());
                                    dsp.getRef().child("sevenRating").setValue(sevenRating.getRating());
                                    dsp.getRef().child("eightRating").setValue(eightRating.getRating());
                                    dsp.getRef().child("nineRating").setValue(nineRating.getRating());
                                    dsp.getRef().child("feedbackSubmitted").setValue(true);
                                }

                                SweetAlertDialog pDialog = new SweetAlertDialog(QuestionActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setContentText(subjectShortName + " feedback form submitted successfully");
                                pDialog.setConfirmText("Ok");
                                pDialog.setConfirmClickListener(sweetAlertDialog -> {
                                    pDialog.dismissWithAnimation();
                                    startActivity(new Intent(QuestionActivity.this, FeedbackActivity.class));
                                });
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(QuestionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(QuestionActivity.this, FeedbackActivity.class));
    }
}