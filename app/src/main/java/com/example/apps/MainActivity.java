package com.example.apps;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //下面这三行参数运行于homework02_main
    EditText weight,height;
    TextView bmiText,show_res;
    Button cal;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homwork01_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        weight =(EditText) findViewById(R.id.input_weight);
//        height = findViewById(R.id.input_height);
//        show_res = findViewById(R.id.textView2);
//        bmiText = findViewById(R.id.show_res);
//        cal = findViewById(R.id.but_get_res);
    }

    @Override
    public void onClick(View v) {
        try {
            String weight_str= weight.getText().toString();
            String height_str= height.getText().toString();
            double w = Double.parseDouble(weight_str);
            double h = Double.parseDouble(height_str);
            // 检查输入是否为空
            if (weight_str.isEmpty() || height_str.isEmpty()) {
                bmiText.setText("请输入有效的体重和身高");
                show_res.setText("输入有误！请检查");
                return;
            }

            // 检查身高是否为零，避免除零错误
            if (h == 0) {
                bmiText.setText("身高不能为零");
                show_res.setText("输入有误！请检查");
                return;
            }
            double  bmi = w/(h*h);
            String res,show;
            if(bmi<18.5 && bmi>0){
                res = "过瘦";
                show ="请注意饮食健康";
            }else if(bmi<24){
                res = "健康";
                show = "请注意保持";
            }else if(bmi<28){
                res ="肥胖";
                show="请多加锻炼";
            }else{
                res = "超重";
                show = "GOOD LUCK";
            }
            bmiText.setText(String.format("您的bmi指数为：%.2f,当前情况：%s",bmi,res));
            show_res.setText(show);
        } catch (Exception e) {
            Log.e(TAG, "onClick: ",e );
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}