package com.example.apps;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        //下面这段是优化前获取传入数据的方法
//        try {
//            intent = getIntent();
//            DOLLAR_RATE = intent.getDoubleExtra("DOLLAR_RATE",0.14);
//            WON_RATE = intent.getDoubleExtra("WON_RATE",200.91);
//            EURO_RATE = intent.getDoubleExtra("EURO_RATE",0.13);
//        } catch (Exception e) {
//            Log.e(TAG, "getIntent: ",e);
//        }
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

//    ActivityResultLauncher luncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            result ->{
//                if(requestCode==6 &&resultCode==3) {
//                    try {
//                        Bundle bn = data.getExtras();
//                        DOLLAR_RATE = bn.getDouble("DOLLAR_RATE", 0.14);
//                        WON_RATE = bn.getDouble("WON_RATE", 200.91);
//                        EURO_RATE = bn.getDouble("EURO_RATE", 0.13);
//                    } catch (Exception e) {
//                        Log.i(TAG, "onActivityResult: 获取数据报错: "+e);
//                    }
//
//                    Log.i(TAG, "onActivityResult: DOLLAR_RATE= " + DOLLAR_RATE);
//                    Log.i(TAG, "onActivityResult: EURO_RATE= " + EURO_RATE);
//                    Log.i(TAG, "onActivityResult: WON_RATE= " + WON_RATE);
//
//                }
//            }
//    )
    public void myConverseClick(View btn){
        //跳转到新页面
        Intent intent = new Intent(this,ConfigSetRatActivity.class);
        intent.putExtra("DOLLAR_RATE",DOLLAR_RATE);
        intent.putExtra("EURO_RATE",EURO_RATE);
        intent.putExtra("WON_RATE",WON_RATE);
        Log.i(TAG, "myConverseClick: DOLLAR_RATE= "+DOLLAR_RATE);
        Log.i(TAG, "myConverseClick: EURO_RATE= "+EURO_RATE);
        Log.i(TAG, "myConverseClick: WON_RATE= "+WON_RATE);

        startActivityForResult(intent,6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //从data中获取数据
        if(requestCode==6 &&resultCode==3) {
            try {
                Bundle bn = data.getExtras();
                DOLLAR_RATE = bn.getDouble("DOLLAR_RATE", 0.14);
                WON_RATE = bn.getDouble("WON_RATE", 200.91);
                EURO_RATE = bn.getDouble("EURO_RATE", 0.13);
            } catch (Exception e) {
                Log.i(TAG, "onActivityResult: 获取数据报错: "+e);
            }

            Log.i(TAG, "onActivityResult: DOLLAR_RATE= " + DOLLAR_RATE);
            Log.i(TAG, "onActivityResult: EURO_RATE= " + EURO_RATE);
            Log.i(TAG, "onActivityResult: WON_RATE= " + WON_RATE);

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}