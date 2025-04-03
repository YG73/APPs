package com.example.apps;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TaxConverseActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText inputRmb;
    private TextView text;
    private Button dollar_btn,won_btn,euro_btn;

    private double DOLLAR_RATE = 0.14,WON_RATE=200.91,EURO_RATE=0.13;

    private Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tax_converse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inputRmb = findViewById(R.id.input_text);
        text = findViewById(R.id.show_test);
        won_btn = findViewById(R.id.won_btn);
        euro_btn = findViewById(R.id.euro_btn);
        dollar_btn = findViewById(R.id.dollar_btn);

        try {
            intent = getIntent();
            DOLLAR_RATE = intent.getDoubleExtra("DOLLAR_RATE",0.14);
            WON_RATE = intent.getDoubleExtra("WON_RATE",200.91);
            EURO_RATE = intent.getDoubleExtra("EURO_RATE",0.13);
        } catch (Exception e) {
            Log.e(TAG, "getIntent: ",e);
        }
    }

    @Override
    public void onClick(View v) {
        //获取输入
        String s = null;
        try {
            s = inputRmb.getText().toString();
        } catch (Exception e) {
            text.setText("输入不合法,请重新输入");
            //消息提示
            Toast.makeText(this, "输入不合法,请重新输入", Toast.LENGTH_SHORT).show();//弹出警告
        }
        double yuan = Double.parseDouble(s);
        String res;
        //计算结果
        if(v.getId()==R.id.dollar_btn){
            res = (yuan*DOLLAR_RATE) + "$";
        } else if (v.getId()==R.id.euro_btn) {
            res = (yuan*EURO_RATE) + "€";
        }else{
            res= (yuan*WON_RATE) +"₩";
        }
        //显示结果
        text.setText(String.valueOf(res));
    }

    public void myConverseClick(View btn){
        //跳转到新页面
        Intent intent = new Intent(this,ConfigSetRatActivity.class);
        intent.putExtra("DOLLAR_RATE",DOLLAR_RATE);
        intent.putExtra("EURO_RATE",EURO_RATE);
        intent.putExtra("WON_RATE",WON_RATE);
        startActivity(intent);
    }

}