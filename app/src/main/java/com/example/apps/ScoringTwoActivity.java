package com.example.apps;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScoringTwoActivity extends AppCompatActivity {

    Button team1_add1,team1_add2,team_add3,team2_add1,team2_add2,team2_add3,reset_btn;
    TextView team1_score,team2_score;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scoring_two);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        team1_add1 = findViewById(R.id.team1_add1);
        team1_add2 = findViewById(R.id.team1_add2);
        team1_add1 = findViewById(R.id.team1_add3);
        team2_add1 = findViewById(R.id.team2_add1);
        team2_add2 = findViewById(R.id.team2_add2);
        team2_add3 = findViewById(R.id.team2_add3);
        reset_btn = findViewById(R.id.reset_btn);
        team1_score = findViewById(R.id.team1_score);
        team2_score = findViewById(R.id.team2_score);
    }
    public void onTowClick(View v) {
        //TEAM A的计分
        String s1 = (String)team1_score.getText();
        int team_a_score = Integer.parseInt(s1);

        //TEAM B的计分
        String s2 = (String)team2_score.getText();
        int team_b_score = Integer.parseInt(s2);


        if(v.getId()==R.id.team1_add1){
            team_a_score++;
        } else if (v.getId()==R.id.team1_add2) {
            team_a_score+=2;
        } else if (v.getId()==R.id.team1_add3) {
            team_a_score +=3;
        } else if (v.getId()==R.id.team2_add1) {
            team_b_score++;
        } else if (v.getId()==R.id.team2_add2) {
            team_b_score+=2;
        } else if (v.getId()==R.id.team2_add3) {
            team_b_score+=3;
        } else if (v.getId()==R.id.reset_btn) {
            team_b_score=0;
            team_a_score=0;
        }

        team1_score.setText(String.valueOf(team_a_score));
        team2_score.setText(String.valueOf(team_b_score));
    }
}