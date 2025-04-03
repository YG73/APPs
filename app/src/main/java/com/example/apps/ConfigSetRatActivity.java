package com.example.apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfigSetRatActivity extends AppCompatActivity{
    private Intent intent;
    private double DOLLAR_RATE = 0,WON_RATE=0,EURO_RATE=0;
    private EditText dor,won,euro;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config_set_rat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intent = getIntent();
        DOLLAR_RATE = intent.getDoubleExtra("DOLLAR_RATE",0.0);
        WON_RATE = intent.getDoubleExtra("WON_RATE",0.0);
        EURO_RATE = intent.getDoubleExtra("EURO_RATE",0.0);
        dor = findViewById(R.id.edit_dollar);
        won = findViewById(R.id.edit_won);
        euro = findViewById(R.id.edit_euro);
        dor.setText(String.valueOf(DOLLAR_RATE));
        won.setText(String.valueOf(WON_RATE));
        euro.setText(String.valueOf(EURO_RATE));
        submit = findViewById(R.id.submit);
    }

    public void onConfigClick(View v) {
        //跳转原来的页面
        Intent intent = new Intent(this,TaxConverseActivity.class);
        String str_d = dor.getText().toString();
        String str_w = euro.getText().toString();
        String str_e = won.getText().toString();

        intent.putExtra("DOLLAR_RATE",Double.parseDouble(str_d));
        intent.putExtra("EURO_RATE",Double.parseDouble(str_w));
        intent.putExtra("WON_RATE",Double.parseDouble(str_e));

        startActivity(intent);
    }
}