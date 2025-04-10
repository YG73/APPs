package com.example.apps;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
        //获得传入数据
        DOLLAR_RATE = intent.getDoubleExtra("DOLLAR_RATE",0.0);
        WON_RATE = intent.getDoubleExtra("WON_RATE",0.0);
        EURO_RATE = intent.getDoubleExtra("EURO_RATE",0.0);
        //获取控件
        dor = findViewById(R.id.edit_dollar);
        won = findViewById(R.id.edit_won);
        euro = findViewById(R.id.edit_euro);
        //显示数据
        dor.setText(String.valueOf(DOLLAR_RATE));
        won.setText(String.valueOf(WON_RATE));
        euro.setText(String.valueOf(EURO_RATE));
        submit = findViewById(R.id.submit);
    }

    public void onConfigClick(View v) {
        try {
            //跳转原来的页面
            Intent intent = getIntent();
            String str_d = dor.getText().toString();
            String str_w = euro.getText().toString();
            String str_e = won.getText().toString();

            Log.i(TAG, "onConfigClick: DOLLAR_RATE= " + str_d);
            Log.i(TAG, "onConfigClick: EURO_RATE= " + str_e);
            Log.i(TAG, "onConfigClick: WON_RATE= " + str_w);

            //使用Bundle封装数据传回原来的页面
            Bundle bn = new Bundle();
            bn.putDouble("DOLLAR_RATE",Double.parseDouble(str_d));
            bn.putDouble("EURO_RATE",Double.parseDouble(str_w));
            bn.putDouble("WON_RATE",Double.parseDouble(str_e));

            intent.putExtras(bn);
            setResult(3,intent);
//        intent.putExtra("DOLLAR_RATE",Double.parseDouble(str_d));
//        intent.putExtra("EURO_RATE",Double.parseDouble(str_w));
//        intent.putExtra("WON_RATE",Double.parseDouble(str_e));

            //保存数据
            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            //下面这是使用SharedPreferences获取数据的示例
//            sp.getFloat("DOLLAR_RATE",0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("DOLLAR_RATE",Float.parseFloat(str_d));
            editor.putFloat("EURO_RATE",Float.parseFloat(str_e));
            editor.putFloat("WON_RATE",Float.parseFloat(str_w));
            editor.apply();

            finish();
        } catch (NumberFormatException e) {
            Log.e(TAG, "出错 onConfigClick: ", e);
        }
    }
}