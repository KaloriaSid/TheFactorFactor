package com.example.factor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Vector;

public class Factors extends AppCompatActivity{

    Vibrator vibrator;

    ConstraintLayout background;

    int correctFactor;
    int scoreStreak;

    private static final long COUNTDOWN_IN_MILLIS = 11_000;

    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";

    private ColorStateList textColorDefaultCd;

    private TextView textViewCountdown;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    public static final String Score = "Score";

    final Vector<Integer> factors = new Vector<>();
    final Vector<Integer> notFactors = new Vector<>();
    Vector<Button> choice = new Vector<>();

    private long backPressedTime;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factors);

        int number;
        number = Objects.requireNonNull(getIntent().getExtras()).getInt("num");
        scoreStreak = getIntent().getExtras().getInt("scoreStreak");

        choice.add((Button) findViewById(R.id.choice1));
        choice.add((Button) findViewById(R.id.choice2));
        choice.add((Button) findViewById(R.id.choice3));
        choice.add((Button) findViewById(R.id.choice4));

        textViewCountdown = findViewById(R.id.countdown);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        background = findViewById(R.id.factors_id);

        textColorDefaultCd = textViewCountdown.getTextColors();

        int c1, c2, c3, c4;
        int index_0, index_1, index_2, index_3;

        Random rand = new Random();

        //Check if the number is prime
        boolean isPrime;
        isPrime = isPrime(number);
        if (isPrime)
            Toast.makeText(this, "You have discovered a prime number.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, number + " is not prime", Toast.LENGTH_SHORT).show();

        //Finding factors
        for (int i=2;i<=number;i++)
            if (number%i==0)
                factors.add(i);
            else
                notFactors.add(i);

        int len_Factors = factors.size();
        int len_notFactors = notFactors.size();

        //Generating random Options
        c1 = rand.nextInt(4);

        do {
            c2 = rand.nextInt(4);
        } while (c2 == c1);

        do {
            c3 = rand.nextInt(4);
        } while (c3 == c2 || c3 == c1);

        do {
            c4 = rand.nextInt(4);
        } while (c4 == c1 || c4 == c2 || c4 == c3);

        //Assigning random values in the options
        int range = len_notFactors - 1 ;
        index_0 = notFactors.get((int) (Math.random() * range));

        do {
            index_1 = notFactors.get((int) (Math.random() * range));
        } while (index_1 == index_0);

        do{
            index_2 = notFactors.get((int) (Math.random() * range));
        } while (index_2 == index_1 || index_2 == index_0);

        do{
            index_3 = notFactors.get((int) (Math.random() * range));
        } while (index_3 == index_2 || index_3 == index_1 || index_3 == index_0);

        choice.get(c1).setText(String.valueOf(index_0));
        choice.get(c2).setText(String.valueOf(index_1));
        choice.get(c3).setText(String.valueOf(index_2));
        choice.get(c4).setText(String.valueOf(index_3));

        //Generating correct option (Randomly)
        range = len_Factors - 1 ;
        byte x = (byte) rand.nextInt(3);
        choice.get(x).setText(String.valueOf(factors.get((int) (Math.random() * range))));
        correctFactor = Integer.parseInt(choice.get(x).getText().toString());
        
        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        if (savedInstanceState != null)
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
        startCountDown();
    }

    //Method for check number is prime or not
    private boolean isPrime(double n) {
        //check if n is a multiple of 2
        if (n%2==0) return false;
        //if not, then just check the odds
        for(int i=3;i<=Math.sqrt(n);i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updteCountDownText();
                checkAnswer();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updteCountDownText();
                scoreStreak = 0;
                createIntent();
                background.setBackgroundColor(Color.RED);
                Toast.makeText(Factors.this, "Correct ans is: " + correctFactor, Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void updteCountDownText() {
        int minutes = (int) (timeLeftInMillis/1_000) / 60;
        int seconds = (int) (timeLeftInMillis/1_000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewCountdown.setText(timeFormatted);

        if (timeLeftInMillis < 6_000){
            textViewCountdown.setTextColor(Color.RED);
        }else{
            textViewCountdown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer() {

        choice.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (factors.contains(Integer.parseInt(choice.get(0).getText().toString()))) {
                    showCorrect();
                    scoreStreak ++;
                } else {
                    vibrateWrongAnswer();
                    showWrong();
                    scoreStreak = 0;
                }
               createIntent();
            }
        });

        choice.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( factors.contains(Integer.parseInt(choice.get(1).getText().toString()))) {
                    showCorrect();
                    scoreStreak ++;
                }
                else {
                    vibrateWrongAnswer();
                    showWrong();
                    scoreStreak = 0;
                }
                createIntent();
            }
        });

        choice.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (factors.contains(Integer.parseInt(choice.get(2).getText().toString()))) {
                    showCorrect();
                    scoreStreak ++;
                }
                else{
                    vibrateWrongAnswer();
                    showWrong();
                    scoreStreak = 0;
                }
                createIntent();
            }
        });

        choice.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( factors.contains(Integer.parseInt(choice.get(3).getText().toString()))) {
                    showCorrect();
                    scoreStreak ++;
                }
                else{
                    vibrateWrongAnswer();
                    showWrong();
                    scoreStreak = 0;
                }
                createIntent();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    private void createIntent() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Score, scoreStreak);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2_000 > System.currentTimeMillis()){
            createIntent();
        }else {
            Toast.makeText(this, "Press again to Finish (Warning: Your score will be 0)", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
    }

    private void showWrong(){
        background.setBackgroundColor(Color.RED);
        Toast.makeText(Factors.this, "Wrong, Correct ans is: " + correctFactor, Toast.LENGTH_SHORT).show();
    }

    private void showCorrect(){
        background.setBackgroundColor(Color.GREEN);
        Toast.makeText(Factors.this, "Correct", Toast.LENGTH_SHORT).show();
    }

    private void vibrateWrongAnswer(){
        vibrator.vibrate(100);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
