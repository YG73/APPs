package com.example.apps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RateCalculateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calculate);

        // 获取传递过来的数据
        String country = getIntent().getStringExtra("country");
        double rate = getIntent().getDoubleExtra("rate", 0.0);

        // 初始化界面组件
        TextView tvCurrencyType = findViewById(R.id.tv_currency_type);
        EditText etRmbAmount = findViewById(R.id.et_rmb_amount);
        Button btnCalculate = findViewById(R.id.btn_calculate);
        TextView tvResult = findViewById(R.id.tv_result);

        // 设置货币类型显示
        tvCurrencyType.setText("人民币兑换" + country);

        // 计算按钮点击事件
        btnCalculate.setOnClickListener(v -> {
            String amountStr = etRmbAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入人民币金额", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double rmbAmount = Double.parseDouble(amountStr);
                double result = rmbAmount * rate;
                tvResult.setText(String.format("%.2f %s", result, country));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
            }
        });
    }
}