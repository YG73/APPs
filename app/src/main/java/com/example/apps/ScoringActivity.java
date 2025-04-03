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

public class ScoringActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView  scoring1;//计分面板
    private Button button1,button2,button3,resetButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scoring);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        scoring1 = findViewById(R.id.textView3);
        button1 = findViewById(R.id.button6);
        button2 = findViewById(R.id.button7);
        button3 = findViewById(R.id.button8);
        resetButton = findViewById(R.id.button9);
    }

    @Override
    public void onClick(View v) {//v代表激活事件的对象
        String s = (String)scoring1.getText();
        int ints = Integer.parseInt(s);
        if(v.getId()==R.id.button6){
            //加1分
            ints++;
        } else if (v.getId()==R.id.button7) {
            //加2分
            ints +=2;
        } else if (v.getId()==R.id.button8) {
            //加3分
            ints+=3;
        }else if(v.getId()==R.id.button9){
            ints = 0;
        }

        scoring1.setText(String.valueOf(ints));
    }
}