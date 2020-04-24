package com.example.factor;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText number;

    int scoreStreakNo = 0;
    int highestStreakNo =0;

    private static final int REQUEST_CODE = 1;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHEST_STREAK = "keyHighestStreak";

    private long backPressedTime;

    TextView scoreStreak;
    TextView highestStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreStreak = findViewById(R.id.Score_Streak_no);
        highestStreak = findViewById(R.id.Highest_Streak_No);
        loadHighestStreak();

        number = findViewById(R.id.number);
        final Button next = findViewById(R.id.next);

        scoreStreakNo = Integer.parseInt(scoreStreak.getText().toString());

    number.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String enteredNumber = number.getText().toString().trim();
            next.setEnabled(!enteredNumber.isEmpty() && !( Double.parseDouble(enteredNumber) <= 10 ));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });

    next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openfactors(); }
    });

    }

    private void openfactors() {
        Intent intent = new Intent(this, Factors.class);
        intent.putExtra("num" , Integer.parseInt(number.getText().toString().trim()));
        intent.putExtra("scoreStreak", scoreStreakNo);
        startActivityForResult(intent, REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK){
                int score = Objects.requireNonNull(data).getIntExtra(Factors.Score, 0);
                 updateScore(score);
            if (score > highestStreakNo)
                updateHigestStreak(score);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadHighestStreak(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highestStreakNo = prefs.getInt(KEY_HIGHEST_STREAK, 0);
        highestStreak.setText(Integer.toString(highestStreakNo));
    }

    @SuppressLint("SetTextI18n")
    private void updateHigestStreak(int highestStreakNoNew) {
        highestStreakNo = highestStreakNoNew;
        highestStreak.setText(Integer.toString(highestStreakNo));

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHEST_STREAK, highestStreakNo);
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    private void updateScore(int scoreStreakNoNew) {
        scoreStreakNo = scoreStreakNoNew;
        scoreStreak.setText(Integer.toString(scoreStreakNo));
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2_000 > System.currentTimeMillis()){
            finish();
        }else {
            Toast.makeText(this, "Press back again to Quit (Warning your Score will be 0)", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}

